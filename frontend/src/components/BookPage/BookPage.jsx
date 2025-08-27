import {useEffect, useState} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import Markdown from "react-markdown";
import classNames from "classnames";
import {fetchBookById} from "../../api/books";
import {getShelf, removeBook, setShelf} from "../../api/library";
import {
    deleteReview,
    getMyReview,
    getReviews,
    postReview
} from "../../api/reviews";

import {useAuth} from "../../context/AuthContext";

import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";

import "./BookPage.css";
import {deleteRating, getMyRating, postRating} from "../../api/rating";

/* ---------- small helpers ---------- */
function Stars({value}) {
    return (
        <div className="rating" aria-hidden="true">
            {Array.from({length: 5}).map((_, i) => (
                <i
                    key={i}
                    className={
                        i < Math.floor(value)
                            ? "fas fa-star"
                            : i < value
                                ? "fas fa-star-half-alt"
                                : "far fa-star"
                    }
                />
            ))}
        </div>
    );
}

function StarPicker({value, onChange, readonly = false, size = 24}) {
    return (
        <div
            className={classNames("rating", {pickable: !readonly})}
            style={{fontSize: size}}
            role="button"
        >
            {Array.from({length: 5}).map((_, i) => (
                <i
                    key={i}
                    className={i < value ? "fas fa-star" : "far fa-star"}
                    onClick={!readonly ? () => onChange(i + 1) : undefined}
                />
            ))}
        </div>
    );
}

export default function BookPage() {
    const {id} = useParams();

    /* --------------------------- state --------------------------- */
    const [book, setBook] = useState(null);
    const [loading, setLoad] = useState(true);
    const [error, setError] = useState(null);

    const {isGuest} = useAuth();

    const [shelf, setShelfState] = useState(null);

    const [myRating, setMyRating] = useState(null);
    const [myReview, setMyReview] = useState("");
    const [myReviewUserId, setMyReviewUserId] = useState(null);
    const [draft, setDraft] = useState("");
    const [editing, setEditing] = useState(false);

    const [reviews, setReviews] = useState([]);
    const [revPage, setRevPage] = useState(0);
    const [revTotal, setRevTotal] = useState(0);
    const navigate = useNavigate();

    /* --------------------------- helpers --------------------------- */
    const shelves = [
        {code: "WANT_TO_READ", label: "Хочу прочитати", icon: "fas fa-bookmark"},
        {code: "CURRENTLY_READING", label: "Читаю", icon: "fas fa-book-open"},
        {code: "READ", label: "Прочитано", icon: "fas fa-check"}
    ];

    /* --------------------------- load book --------------------------- */
    useEffect(() => {
        setLoad(true);
        fetchBookById(id)
            .then(res => setBook(res.data))
            .catch(() => setError("Книгу не знайдено"))
            .finally(() => setLoad(false));
    }, [id]);

    /* --------------------------- shelf --------------------------- */
    useEffect(() => {
        if (isGuest) return;
        getShelf(id).then(res => res.status === 200 && setShelfState(res.data));
    }, [id, isGuest]);

    const handleMove = code => {
        if (isGuest) return navigate("/login");
        setShelf(id, code).then(() => setShelfState(code));
    };

    /* --------------------------- rating --------------------------- */
    useEffect(() => {
        if (isGuest) return;
        getMyRating(id).then(r => r.status === 200 && setMyRating(r.data));
    }, [id, isGuest]);

    const handleRate = score => {
        if (isGuest) return navigate("/login");
        if (score === myRating) {
            deleteRating(id).then(() => setMyRating(null));
        } else {
            postRating(id, score).then(() => setMyRating(score));
        }
    };



    // завантажуємо всі відгуки й відфільтровуємо ваш
    const loadReviews = (p = 0) => {
        getReviews(id, p).then(r => {
            let list = r.data.content;
            if (myReviewUserId !== null) {
                list = list.filter(rv => rv.userId !== myReviewUserId);
            }
            setReviews(list);
            setRevPage(r.data.page.number);
            setRevTotal(r.data.page.totalPages);
        });
    };

    // перезапуск після зміни id чи вашого userId
    useEffect(() => {
        loadReviews();
    }, [id, myReviewUserId]);

    useEffect(() => {
        if (isGuest) return;

        getMyReview(id).then(r => {
            if (r.status === 200) {
                setMyReview(r.data.content);
                setDraft(r.data.content);
                setMyReviewUserId(r.data.userId);
            }
        });
    }, [id, isGuest]);

    const handleReviewSubmit = e => {
        e.preventDefault();
        if (isGuest) return navigate("/login");
        if (!draft.trim()) return;
        postReview(id, draft).then(() => {
            setMyReview(draft);
            setEditing(false);
            loadReviews();
        });
    };

    const handleDeleteReview = () => {
        if (isGuest) return navigate("/login");
        deleteReview(id).then(() => {
            setMyReview("");
            setDraft("");
            setEditing(false);
            loadReviews();
        });
    };

    /* --------------------------- view states --------------------------- */
    if (loading) {
        return (
            <>
                <Navbar/>
                <div className="container mt-5">Завантаження…</div>
            </>
        );
    }

    if (error) {
        return (
            <>
                <Navbar/>
                <div className="container mt-5 text-danger">{error}</div>
            </>
        );
    }

    /* --------------------------- render --------------------------- */
    return (
        <>
            <Navbar/>

            {/* breadcrumbs */}
            <nav className="container mt-3" aria-label="breadcrumb">
                <ol className="breadcrumb">
                    <li className="breadcrumb-item">
                        <a href="/">Головна</a>
                    </li>
                    <li className="breadcrumb-item">
                        <a href="/catalog">Каталог</a>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                        {book.title}
                    </li>
                </ol>
            </nav>

            {/* main */}
            <div className="container mt-4">
                <div className="row">
                    {/* left */}
                    <div className="col-md-3 mb-2 text-center pe-3">
                        <img
                            src={book.cover}
                            alt={book.title}
                            className="book-cover img-fluid mb-4"
                        />


                        <div className="dropdown d-grid">
                            <button
                                className="btn btn-primary dropdown-toggle"
                                data-bs-toggle="dropdown"
                                type="button"
                            >
                                {shelves.find(s => s.code === shelf)?.label ||
                                    "Додати до бібліотеки"}
                            </button>

                            <ul className="dropdown-menu w-100">
                                {shelves.map(s => (
                                    <li key={s.code}>
                                        <button
                                            className={classNames("dropdown-item", {
                                                active: shelf === s.code
                                            })}
                                            type="button"
                                            onClick={() => handleMove(s.code)}
                                        >
                                            <i className={`${s.icon} me-2`}/>
                                            {s.label}
                                        </button>
                                    </li>
                                ))}

                                {shelf && (
                                    <>
                                        <li>
                                            <hr className="dropdown-divider"/>
                                        </li>
                                        <li>
                                            <button
                                                className="dropdown-item text-danger"
                                                type="button"
                                                onClick={() =>
                                                    isGuest ? navigate("/login") : removeBook(id).then(() => setShelfState(null))
                                                }
                                            >
                                                <i className="fas fa-times me-2"/>
                                                Прибрати з бібліотеки
                                            </button>
                                        </li>
                                    </>
                                )}
                            </ul>
                        </div>
                    </div>

                    {/* right */}
                    <div className="col-md-9 book-info-container">

                        {/* series with number */}
                        {book.series && (
                            <p className="book-series mb-1">
                                <a href={`/series/${book.series.id}`} className="series-link">
                                    <em>
                                        {book.series.name}
                                        {book.seriesNumber != null && `, №${book.seriesNumber}`}
                                    </em>
                                </a>
                            </p>
                        )}

                        <h1 className="book-title">{book.title}</h1>

                        <h5 className="author">
                            <Link to={`/authors/${book.author.id}`} className="author-link">
                                {book.author.firstName} {book.author.lastName}
                            </Link>
                        </h5>

                        <div className="rating-container">
                            <Stars value={book.averageRating}/>
                            <span className="rating-number">
                {book.averageRating.toFixed(1)}
              </span>
                            <span className="rating-count">
                {book.ratingCount} оцінок • {book.reviewCount} відгуків
              </span>
                        </div>

                        <div className="mt-2 mb-2">
                            <Markdown>{book.annotation}</Markdown>
                        </div>

                        <Badges label="Жанри:" items={book.genres} cls="genre-badge"/>
                        <Badges label="Тропи:" items={book.tropes} cls="trope-badge"/>

                        {/* details */}
                        <div className="row mt-3">
                            <div className="col-md-6">
                                <Detail label="Дата публікації:" value={book.publicationDate}/>
                                <Detail label="Сторінок:" value={book.pageCount}/>
                                <Detail label="Мова:" value={book.language}/>
                            </div>
                            <div className="col-md-6">
                                <Detail label="ISBN:" value={book.isbn}/>
                                <Detail label="Видання:" value={book.edition}/>
                                <Detail label="Видавець:" value={book.publisher}/>
                            </div>
                        </div>
                    </div>
                </div>

                <hr className="section-divider"/>

                {/* reviews */}
                <h3 className="mb-4">Відгуки</h3>

                <div className="card review-form-card mb-4">
                    <div className="card-body">
                        <h5 className="card-title mb-3">
                            {myReview && !editing ? "Ваш відгук" : "Додати відгук"}
                        </h5>

                        <StarPicker value={myRating ?? 0} onChange={handleRate}/>

                        {myReview && !editing && (
                            <>
                                <p className="mt-3 mb-0">{myReview}</p>
                                <button
                                    className="btn btn-outline-primary mt-3"
                                    type="button"
                                    onClick={() => {
                                        setEditing(true);
                                        setDraft(myReview);
                                    }}
                                >
                                    Редагувати
                                </button>
                            </>
                        )}

                        {(!myReview || editing) && (
                            <form onSubmit={handleReviewSubmit}>
                  <textarea
                      rows={4}
                      className="form-control my-3"
                      placeholder="Напишіть свої враження про книгу…"
                      value={draft}
                      onChange={e => setDraft(e.target.value)}
                  />

                                <button className="btn btn-primary" type="submit">
                                    {myReview ? "Зберегти" : "Відправити"}
                                </button>

                                {myReview && (
                                    <button
                                        className="btn btn-outline-danger ms-2"
                                        type="button"
                                        onClick={handleDeleteReview}
                                    >
                                        Видалити
                                    </button>
                                )}
                            </form>
                        )}
                    </div>
                </div>


                {reviews.map(rv => (
                    <div key={rv.userId} className="card review-card">
                        <div className="card-body">
                            <div className="review-header">
                                <div>
                                    <span className="fw-bold me-2">{rv.userName}</span>
                                    <StarPicker value={rv.rating} readonly size={16}/>
                                </div>
                                <span className="review-date">
                  {new Date(rv.addedAt).toLocaleDateString("uk-UA", {
                      day: "2-digit",
                      month: "long",
                      year: "numeric"
                  })}
                </span>
                            </div>
                            <p className="mb-0">{rv.content}</p>
                        </div>
                    </div>
                ))}
                <ReviewPager page={revPage} total={revTotal} onChange={loadReviews}/>
            </div>

            <Footer/>
        </>
    );
}

/* ---------- helpers ---------- */

function Badges({label, items, cls}) {
    if (!items?.length) return null;
    return (
        <div className="badge-container">
            <span className="badge-label">{label}</span>
            <div>
                {items.map(i => (
                    <span key={i.id} className={`badge ${cls} px-3 py-2`}>
            {i.name}
          </span>
                ))}
            </div>
        </div>
    );
}

function Detail({label, value}) {
    if (value === null || value === undefined || value === "") return null;
    return (
        <div className="book-detail">
            <span className="book-details-label">{label}</span> {value}
        </div>
    );
}

function ReviewPager({page, total, onChange}) {
    if (total < 2) return null;
    return (
        <nav aria-label="review-pages">
            <ul className="pagination justify-content-center">
                <li className={`page-item ${page === 0 ? "disabled" : ""}`}>
                    <button
                        className="page-link"
                        type="button"
                        onClick={() => onChange(page - 1)}
                    >
                        &laquo;
                    </button>
                </li>

                {Array.from({length: total}).map((_, i) => (
                    <li key={i} className={`page-item ${i === page ? "active" : ""}`}>
                        <button
                            className="page-link"
                            type="button"
                            onClick={() => onChange(i)}
                        >
                            {i + 1}
                        </button>
                    </li>
                ))}

                <li className={`page-item ${page === total - 1 ? "disabled" : ""}`}>
                    <button
                        className="page-link"
                        type="button"
                        onClick={() => onChange(page + 1)}
                    >
                        &raquo;
                    </button>
                </li>
            </ul>
        </nav>
    );
}
