export function Spinner() {
    return (
        <div className="spinner-wrap" role="status" aria-label="Loading">
            <span className="spinner" />
        </div>
    );
}

export function PageHeader({ title, subtitle, children }) {
    return (
        <div className="page-header">
            <div>
                <h1>{title}</h1>
                {subtitle && <p className="muted">{subtitle}</p>}
            </div>
            {children}
        </div>
    );
}
