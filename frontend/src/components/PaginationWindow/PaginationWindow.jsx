import PropTypes from "prop-types";
import classNames from "classnames";

export default function PaginationWindow({currentPage, totalPages, onChange, radius = 2}) {
    if (totalPages < 2) return null;

    const getPageWindow = (current, total, r) => {
        const window = [];
        window.push(0);
        const from = Math.max(1, current - r);
        if (from > 1) window.push("left-ellipsis");

        for (let i = from; i <= Math.min(current + r, total - 2); i++) {
            window.push(i);
        }

        if (current + r < total - 2) window.push("right-ellipsis");

        if (total > 1) window.push(total - 1);

        return window;
    };

    const pageWindow = getPageWindow(currentPage, totalPages, radius);

    return (
        <nav aria-label="pagination">
            <ul className="pagination justify-content-center">
                <li className={classNames("page-item", {disabled: currentPage === 0})}>
                    <button className="page-link" onClick={() => onChange(currentPage - 1)}>&laquo;</button>
                </li>

                {pageWindow.map((p, idx) =>
                    p === "left-ellipsis" || p === "right-ellipsis" ? (
                        <li key={p + idx} className="page-item disabled">
                            <span className="page-link">…</span>
                        </li>
                    ) : (
                        <li key={p} className={classNames("page-item", {active: p === currentPage})}>
                            <button className="page-link" onClick={() => onChange(p)}>
                                {p + 1}
                            </button>
                        </li>
                    )
                )}

                <li className={classNames("page-item", {disabled: currentPage >= totalPages - 1})}>
                    <button className="page-link" onClick={() => onChange(currentPage + 1)}>&raquo;</button>
                </li>
            </ul>
        </nav>
    );
}

PaginationWindow.propTypes = {
    currentPage: PropTypes.number.isRequired,
    totalPages: PropTypes.number.isRequired,
    onChange: PropTypes.func.isRequired,
    radius: PropTypes.number
};
