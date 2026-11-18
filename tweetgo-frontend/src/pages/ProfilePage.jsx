import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { FiMapPin } from "react-icons/fi";
import API, { errorMessage } from "../api";
import { useAuth } from "../AuthContext";
import Avatar from "../components/Avatar";
import Feed from "../components/Feed";
import FollowButton from "../components/FollowButton";
import { Spinner } from "../components/ui";

function EditProfileForm({ profile, onSaved, onCancel }) {
    const [values, setValues] = useState({
        fullName: profile.fullName || "",
        bio: profile.bio || "",
        address: profile.address || "",
    });
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const set = (key) => (e) => setValues({ ...values, [key]: e.target.value });

    const save = async (e) => {
        e.preventDefault();
        setSaving(true);
        setError("");
        try {
            const res = await API.put("/api/users/me", {
                fullName: values.fullName.trim(),
                bio: values.bio.trim(),
                address: values.address.trim(),
            });
            onSaved(res.data);
        } catch (err) {
            setError(errorMessage(err, "Could not save your profile."));
            setSaving(false);
        }
    };

    return (
        <form className="form edit-form" onSubmit={save}>
            <label>
                Name
                <input value={values.fullName} onChange={set("fullName")} maxLength={100} placeholder="Your full name" />
            </label>
            <label>
                Bio
                <textarea value={values.bio} onChange={set("bio")} maxLength={500} rows={3} placeholder="A few words about you" />
            </label>
            <label>
                Location
                <input value={values.address} onChange={set("address")} maxLength={200} placeholder="e.g. Regensburg, Germany" />
            </label>
            {error && <p className="form-error">{error}</p>}
            <div className="row-end">
                <button type="button" className="btn btn-ghost" onClick={onCancel}>Cancel</button>
                <button className="btn btn-primary" disabled={saving}>{saving ? "Saving…" : "Save"}</button>
            </div>
        </form>
    );
}

export default function ProfilePage() {
    const { username } = useParams();
    const { me, setMe } = useAuth();
    const [profile, setProfile] = useState(null);
    const [error, setError] = useState("");
    const [editing, setEditing] = useState(false);
    const isMe = me?.username === username;

    useEffect(() => {
        setProfile(null);
        setError("");
        setEditing(false);
        API.get(`/api/users/${encodeURIComponent(username)}`)
            .then((res) => setProfile(res.data))
            .catch((err) => setError(err.response?.status === 404 ? "This user doesn't exist." : errorMessage(err)));
    }, [username]);

    if (error) return <div className="card notice"><p>{error}</p></div>;
    if (!profile) return <div className="card"><Spinner /></div>;

    return (
        <>
            <section className="card profile">
                <div className="profile-top">
                    <Avatar username={profile.username} size={76} />
                    {isMe ? (
                        !editing && <button className="btn btn-outline" onClick={() => setEditing(true)}>Edit profile</button>
                    ) : (
                        <FollowButton user={profile} onChange={setProfile} />
                    )}
                </div>

                {editing ? (
                    <EditProfileForm
                        profile={profile}
                        onCancel={() => setEditing(false)}
                        onSaved={(updated) => {
                            setProfile(updated);
                            setMe(updated);
                            setEditing(false);
                        }}
                    />
                ) : (
                    <>
                        <h1 className="profile-name">{profile.fullName || profile.username}</h1>
                        <p className="muted">@{profile.username}</p>
                        {profile.bio && <p className="profile-bio">{profile.bio}</p>}
                        {!profile.bio && isMe && <p className="muted small">Add a bio so people know who you are.</p>}
                        {profile.address && (
                            <p className="muted small icon-text"><FiMapPin /> {profile.address}</p>
                        )}
                        <p className="profile-stats">
                            <span><strong>{profile.followingCount}</strong> Following</span>
                            <span><strong>{profile.followersCount}</strong> Followers</span>
                        </p>
                    </>
                )}
            </section>

            <h2 className="section-title">Posts</h2>
            <Feed
                url={`/api/tweet/user/${encodeURIComponent(profile.username)}`}
                empty={<p className="muted">{isMe ? "You haven't posted anything yet." : `@${profile.username} hasn't posted yet.`}</p>}
            />
        </>
    );
}
