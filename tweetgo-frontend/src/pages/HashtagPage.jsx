import { useParams } from "react-router-dom";
import Feed from "../components/Feed";
import { PageHeader } from "../components/ui";

export default function HashtagPage() {
    const { name } = useParams();
    return (
        <>
            <PageHeader title={`#${name}`} subtitle="Posts with this hashtag" />
            <Feed url={`/api/tweet/hashtag/${encodeURIComponent(name)}`} empty={<p className="muted">No posts with #{name} yet.</p>} />
        </>
    );
}
