import { useState } from "react";
import { Link } from "react-router-dom";
import Composer from "../components/Composer";
import Feed from "../components/Feed";
import { PageHeader } from "../components/ui";

export default function HomePage() {
    const [newTweet, setNewTweet] = useState(null);

    return (
        <>
            <PageHeader title="Home" subtitle="Your posts and posts from people you follow" />
            <Composer onPosted={setNewTweet} />
            <Feed
                url="/api/tweet/feed"
                prepend={newTweet}
                empty={
                    <>
                        <p><strong>Your feed is empty.</strong></p>
                        <p className="muted">
                            Write your first post above, or find people to follow in{" "}
                            <Link to="/explore">Explore</Link> and <Link to="/search">Search</Link>.
                        </p>
                    </>
                }
            />
        </>
    );
}
