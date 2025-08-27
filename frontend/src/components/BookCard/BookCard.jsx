import PropTypes from "prop-types";
import {Link} from "react-router-dom";
import "./BookCard.css";
import {useBookCard} from "../../hooks/useBookCard";

export default function BookCard({book}) {
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
                <div className="card-body">
                    <h5 className="card-title">{book.title}</h5>
                    <p className="card-author">
                        <Link to={`/authors/${book.author.id}`} className="author-link">
                            {book.author.firstName} {book.author.lastName}
                        </Link>
                    </p>
                    <div className="rating">
                        {Array.from({length: 5}, (_, i) => (
                            <i key={`star-${i}`} className={renderStar(i)}/>
                        ))}
                        <span className="text-dark ms-1">
                            {book.averageRating.toFixed(1)}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    );
}

BookCard.propTypes = {
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
};
