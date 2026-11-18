import Feed from "../components/Feed";
import { PageHeader } from "../components/ui";

export default function ExplorePage() {
    return (
        <>
            <PageHeader title="Explore" subtitle="The latest posts from everyone" />
            <Feed url="/api/tweet/explore" empty={<p className="muted">No posts yet. Be the first!</p>} />
        </>
    );
}
