#!/usr/bin/env bash
# Keeps trying to create an Always Free ARM VM (VM.Standard.A1.Flex) on Oracle Cloud
# until capacity is available. Run it in Oracle Cloud Shell (OCI CLI is preinstalled):
#   curl -fsSL https://raw.githubusercontent.com/Harry24x/Tweetgo/master/deploy/oracle-retry.sh | bash
# Stays inside the Always Free limits (1 OCPU / 6 GB by default).
set -uo pipefail

OCPUS="${OCPUS:-1}"
MEMORY_GB="${MEMORY_GB:-6}"
WAIT_SECONDS="${WAIT_SECONDS:-60}"
C="${OCI_TENANCY:?Run this in Oracle Cloud Shell}"
KEY="$HOME/.ssh/tweetgo"

# SSH key lives in Cloud Shell, so you can connect from there later: ssh -i ~/.ssh/tweetgo ubuntu@<IP>
[ -f "$KEY" ] || ssh-keygen -t ed25519 -N "" -f "$KEY" -q

SUBNET=$(oci network subnet list -c "$C" --all 2>/dev/null \
  | jq -r '[.data[] | select(."prohibit-public-ip-on-vnic" == false)][0].id // empty')
if [ -z "$SUBNET" ]; then
  echo "No public subnet found. In the console open Networking > Virtual cloud networks >"
  echo "Start VCN Wizard > 'Create VCN with Internet Connectivity', then run this again."
  exit 1
fi

IMAGE=$(oci compute image list -c "$C" --operating-system "Canonical Ubuntu" \
  --operating-system-version "24.04" --shape VM.Standard.A1.Flex \
  --sort-by TIMECREATED --sort-order DESC --all | jq -r '.data[0].id')
ADS=$(oci iam availability-domain list -c "$C" | jq -r '.data[].name')

echo "Subnet: $SUBNET"
echo "Image:  $IMAGE"
echo "Trying $OCPUS OCPU / $MEMORY_GB GB every ${WAIT_SECONDS}s. Keep this tab open. Stop with Ctrl+C."

attempt=0
while true; do
  for AD in $ADS; do
    attempt=$((attempt + 1))
    echo "$(date +%H:%M:%S)  attempt $attempt  $AD"
    if oci compute instance launch -c "$C" --availability-domain "$AD" \
        --shape VM.Standard.A1.Flex \
        --shape-config "{\"ocpus\": $OCPUS, \"memoryInGBs\": $MEMORY_GB}" \
        --image-id "$IMAGE" --subnet-id "$SUBNET" --assign-public-ip true \
        --display-name tweetgo --ssh-authorized-keys-file "$KEY.pub" \
        --wait-for-state RUNNING > /tmp/tweetgo-launch.json 2> /tmp/tweetgo-err.txt; then
      ID=$(jq -r '.data.id' /tmp/tweetgo-launch.json)
      IP=$(oci compute instance list-vnics --instance-id "$ID" | jq -r '.data[0]."public-ip"')
      echo
      echo "SUCCESS! Server is running."
      echo "Public IP: $IP"
      echo "Connect from this Cloud Shell with:  ssh -i ~/.ssh/tweetgo ubuntu@$IP"
      exit 0
    fi
    if ! grep -qiE "capacity|TooManyRequests|InternalError" /tmp/tweetgo-err.txt; then
      echo "Unexpected error (not a capacity problem):"
      cat /tmp/tweetgo-err.txt
      exit 1
    fi
  done
  sleep "$WAIT_SECONDS"
done
