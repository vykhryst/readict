import PropTypes from "prop-types";
import {Link} from "react-router-dom";
import classNames from "classnames";
import "./RecommendationsPage.css";
import {useBookCard} from "../../hooks/useBookCard";

export default function RecommendationCard({book, shelfMap, shelves, onMove, onRemove, onSkip}) {
    const {rating, renderStar, handleCardClick, handleKeyDown} = useBookCard(book);

    return (
        <div className="col">
            <div
                className="card book-card h-100"
                role="button"
                tabIndex={0}
                onClick={handleCardClick}
                onKeyDown={handleKeyDown}
            >
                <div className="book-img-container">
                    <img src={book.cover} alt={book.title} className="book-img"/>
                </div>

                <div className="card-body d-flex flex-column">
                    <h5 className="card-title">{book.title}</h5>
                    <p className="card-author">
                        <Link to={`/authors/${book.author.id}`} className="author-link">
                            {book.author.firstName} {book.author.lastName}
                        </Link>
                    </p>

                    <div className="rating mb-2">
                        {Array.from({length: 5}, (_, i) => (
                            <i key={`star-${i}`} className={renderStar(i)}/>
                        ))}
                        <span className="text-dark ms-1">
                            {book.averageRating.toFixed(1)}
                        </span>
                    </div>

                    {/* dropdown зі статусом */}
                    <div className="dropdown mb-2">
                        <button
                            className="btn btn-primary dropdown-toggle w-100"
                            data-bs-toggle="dropdown"
                        >
                            {shelves.find((s) => s.code === shelfMap[book.id])?.label ||
                                "Додати до бібліотеки"}
                        </button>
                        <ul className="dropdown-menu w-100">
                            {shelves.map((s) => (
                                <li key={s.code}>
                                    <button
                                        className={classNames("dropdown-item", {
                                            active: shelfMap[book.id] === s.code,
                                        })}
                                        onClick={() => onMove(book.id, s.code)}
                                    >
                                        <i className={`${s.icon} me-2`}/>
                                        {s.label}
                                    </button>
                                </li>
                            ))}
                            {shelfMap[book.id] && (
                                <>
                                    <li>
                                        <hr className="dropdown-divider"/>
                                    </li>
                                    <li>
                                        <button
                                            className="dropdown-item text-danger"
                                            onClick={() => onRemove(book.id)}
                                        >
                                            <i className="fas fa-times me-2"/>
                                            Видалити з бібліотеки
                                        </button>
                                    </li>
                                </>
                            )}
                        </ul>
                    </div>

                    {/* кнопка “Не цікавить” */}
                    <button
                        className="btn btn-outline-secondary mt-auto"
                        onClick={() => onSkip(book.id)}
                    >
                        Не цікавить
                    </button>
                </div>
            </div>
        </div>
    );
}

RecommendationCard.propTypes = {
    book: PropTypes.shape({
        id: PropTypes.number.isRequired,
        title: PropTypes.string.isRequired,
        cover: PropTypes.string.isRequired,
        averageRating: PropTypes.number.isRequired,
        author: PropTypes.shape({
            id: PropTypes.number.isRequired,
            firstName: PropTypes.string.isRequired,
            lastName: PropTypes.string.isRequired,
        }).isRequired,
    }).isRequired,
    shelfMap: PropTypes.object.isRequired,
    shelves: PropTypes.array.isRequired,
    onMove: PropTypes.func.isRequired,
    onRemove: PropTypes.func.isRequired,
    onSkip: PropTypes.func.isRequired,
};
