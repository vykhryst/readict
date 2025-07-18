import {Link} from "react-router-dom";
import "./BookCard.css"; // стилі картки

export default function BookCard({book}) {
    const rating = Math.round(book.averageRating * 2) / 2;   // до .0 / .5
    return (
        <div className="col">
            <Link to={`/books/${book.id}`} className="text-decoration-none">
                <div className="card book-card h-100">
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
                            {Array.from({length: 5}).map((_, i) => (
                                <i key={i}
                                   className={
                                       i + 1 <= rating
                                           ? "fas fa-star"
                                           : i + 0.5 === rating
                                               ? "fas fa-star-half-alt"
                                               : "far fa-star"
                                   }/>
                            ))}
                            <span className="text-dark ms-1" >{book.averageRating.toFixed(1)}</span>
                        </div>
                    </div>
                </div>
            </Link>
        </div>
    );
}
