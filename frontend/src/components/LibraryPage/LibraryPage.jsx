import {useEffect, useMemo, useState} from "react";
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";
import {getLibraryPage, getLibrarySummary, removeBook, setShelf} from "../../api/library";
import {fetchGenres} from "../../api/books";
import "./LibraryPage.css";
import classNames from "classnames";
import ReviewModal from "../ReviewModal/ReviewModal";
import {Link} from "react-router-dom";
import {deleteRating, postRating} from "../../api/rating";

export default function LibraryPage() {
    const [summary, setSummary] = useState(null);
    const [genres, setGenres] = useState([]);
    const [filter, setFilter] = useState({
        shelf: null,
        search: "",
        genres: new Set(),
        rating: null,
        yearFrom: null,
        yearTo: null,
        sort: "ADDED_AT_DESC",
        pageSize: 6,
        page: 0
    });
    const [page, setPage] = useState(null);
    const [loading, setLoading] = useState(true);
    const [activeReviewBook, setActiveReviewBook] = useState(null); // For managing the modal

    /* --- fetch summary & genres once --- */
    useEffect(() => {
        getLibrarySummary().then(r => setSummary(r.data));

        fetchGenres().then(r => {
            const list = Array.isArray(r.data) ? r.data
                : (r.data?.content ?? []);
            setGenres(list);
        });
    }, []);

    /* --- fetch page whenever filter changes --- */
    useEffect(() => {
        setLoading(true);

        getLibraryPage({
            page: filter.page,
            size: filter.pageSize,
            sort: filter.sort,
            shelf: filter.shelf,
            search: filter.search || undefined,
            rating: filter.rating ?? undefined,
            yearFrom: filter.yearFrom ?? undefined,
            yearTo: filter.yearTo ?? undefined,
            genres: Array.from(filter.genres).map(Number)
        })
            .then(r => setPage(r.data))
            .finally(() => setLoading(false));
    }, [filter]);

    /* ---------- pagination window function ---------- */
    function getPageWindow(current, total, radius = 2) {
        const window = [];

        // завжди перша
        window.push(0);

        // блок ліворуч
        const from = Math.max(1, current - radius);
        if (from > 1) window.push("left-ellipsis");

        for (let i = from; i <= Math.min(current + radius, total - 2); i++) {
            window.push(i);
        }

        // блок праворуч
        if (current + radius < total - 2) window.push("right-ellipsis");

        // завжди остання, якщо > 1 стор.
        if (total > 1) window.push(total - 1);

        return window;
    }

    /* ---------- helpers ---------- */
    const shelves = {
        ALL: "Усі книги",
        READ: "Прочитано",
        CURRENTLY_READING: "Читаю",
        WANT_TO_READ: "Хочу прочитати"
    };

    const changeShelf = (bookId, shelf) =>
        setShelf(bookId, shelf).then(() =>
            setFilter({...filter}));

    const deleteFromLibrary = bookId =>
        removeBook(bookId).then(() =>
            setFilter({...filter}));

    const changeRating = (bookId, oldScore, newScore) => {
        const req = oldScore === newScore    // другий клік по тій самій зірці → скинути
            ? deleteRating(bookId)
            : postRating(bookId, newScore);

        req.then(() => {
            // Update the rating in the UI immediately without a full page reload
            if (page) {
                const updatedContent = page.content.map(book => {
                    if (book.id === bookId) {
                        return {
                            ...book,
                            myRating: oldScore === newScore ? null : newScore
                        };
                    }
                    return book;
                });

                setPage({
                    ...page,
                    content: updatedContent
                });
            }
        });
    };

    // Modal handlers
    const openReviewModal = (book) => {
        setActiveReviewBook(book);
    };

    const closeReviewModal = () => {
        setActiveReviewBook(null);
    };

    const handleReviewSave = (updatedBook) => {
        // Update the review in the UI immediately without a full page reload
        if (page) {
            const updatedContent = page.content.map(book => {
                if (book.id === updatedBook.id) {
                    return {
                        ...book,
                        review: updatedBook.review,
                        myRating: updatedBook.myRating
                    };
                }
                return book;
            });

            setPage({
                ...page,
                content: updatedContent
            });
        }
    };

    // Pagination metadata
    const currentPage = page?.page?.number ?? 0;
    const totalPages = page?.page?.totalPages ?? 0;

    // Мемоізоване вікно пагінації
    const pageWindow = useMemo(
        () => getPageWindow(currentPage, totalPages),
        [currentPage, totalPages]
    );

    /* ---------- render ---------- */
    return (
        <>
            <Navbar/>
            <div className="container mt-4 library-page">

                {/* -------- заголовок + статистика -------- */}
                {summary && (
                    <div className="library-header">
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <h1 className="mb-0">Моя бібліотека</h1>
                            <a href={`/recommendations`} className="btn btn-outline-primary"><i
                                className="fas fa-lightbulb me-2"></i>Рекомендації</a>
                        </div>

                        <div className="row library-stats">
                            <Stat n={summary.total} label="Всього книг"/>
                            <Stat n={summary.read} label="Прочитано"/>
                            <Stat n={summary.reading} label="Зараз читаю"/>
                            <Stat n={summary.want} label="Хочу прочитати"/>
                        </div>
                    </div>
                )}

                <div className="row">
                    <div className="col-lg-9 order-lg-2">

                        {/* -------- tabs (полиці) -------- */}
                        <ul className="nav nav-tabs section-tabs mb-3">
                            {Object.entries(shelves).map(([code, label]) => (
                                <li className="nav-item" key={code}>
                                    <button
                                        className={classNames("nav-link", {
                                            active: filter.shelf === (code === "ALL" ? null : code)
                                        })}
                                        onClick={() =>
                                            setFilter({...filter, shelf: code === "ALL" ? null : code, page: 0})}
                                    >
                                        {label}
                                    </button>
                                </li>
                            ))}
                        </ul>

                        {/* -------- top bar -------- */}
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            {page && (() => {
                                const {number, size, totalElements} = page.page;
                                const from = number * size + 1;
                                const to = number * size + page.content.length;
                                return (
                                    <span className="text-muted">{from}-{to} із {totalElements} книг</span>);
                            })()}

                            <div className="d-flex align-items-center">
                                <label className="me-2 text-muted small">Сортувати:</label>
                                <select
                                    className="form-select form-select-sm"
                                    value={filter.sort}
                                    onChange={e => setFilter({...filter, sort: e.target.value})}
                                >
                                    <option value="TITLE_ASC">Назва (А-Я)</option>
                                    <option value="TITLE_DESC">Назва (Я-А)</option>
                                    <option value="ADDED_AT_DESC">Дата додавання (нові спочатку)</option>
                                    <option value="ADDED_AT_ASC">Дата додавання (старі спочатку)</option>
                                    <option value="AUTHOR_ASC">Автор (А-Я)</option>
                                    <option value="AUTHOR_DESC">Автор (Я-А)</option>
                                    <option value="RATING_DESC">Моя оцінка (спадання)</option>
                                    <option value="RATING_ASC">Моя оцінка (зростання)</option>
                                </select>
                            </div>
                        </div>

                        {/* -------- table -------- */}
                        {loading && <p>Завантаження…</p>}
                        {!loading && page && page.content.length === 0 && (
                            <p className="text-muted">Нічого не знайдено.</p>
                        )}

                        {!loading && page && page.content.length > 0 && (
                            <div className="library-table">
                                <div className="table-responsive">
                                    <table className="table align-middle">
                                        <thead>
                                        <tr>
                                            <th style={{width: 70}}></th>
                                            <th>Назва</th>
                                            <th>Автор</th>
                                            <th className="column-rating">Моя оцінка</th>
                                            <th className="date-added">Дата</th>
                                            <th className="book-review">Рецензія</th>
                                            <th className="column-status">Полиця</th>
                                            <th style={{width: 50}}></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {page.content.map(b => (
                                            <tr key={b.id}>
                                                <td>
                                                    <img src={b.cover}
                                                         alt={b.title}
                                                         className="book-cover-small"/>
                                                </td>
                                                <td>
                                                    <a href={`/books/${b.id}`} className="book-title">{b.title}</a>
                                                </td>
                                                <td><Link to={`/authors/${b.authorId}`} className="author-cell">
                                                    {b.author}</Link>
                                                </td>
                                                <td className="text-center column-rating">
                                                    <Stars
                                                        score={b.myRating ?? 0}
                                                        onChange={s => changeRating(b.id, b.myRating, s)}
                                                    />
                                                </td>

                                                <td className="date-added">
                            <span className="text-muted">
                              {new Date(b.addedAt).toLocaleDateString("uk-UA")}
                            </span>
                                                </td>
                                                <td className="col-review">
                                                    {b.review ? (
                                                        <>
                                                            <div className="review-text">{b.review}</div>
                                                            <button
                                                                className="review-btn"
                                                                onClick={() => openReviewModal(b)}
                                                            >
                                                                Редагувати
                                                            </button>
                                                        </>
                                                    ) : (
                                                        <button
                                                            className="review-btn"
                                                            onClick={() => openReviewModal(b)}
                                                        >
                                                            Додати
                                                        </button>
                                                    )}
                                                </td>
                                                <td>
                                                    <select
                                                        className="shelf-select form-select form-select-sm"
                                                        value={b.shelf}
                                                        onChange={e => changeShelf(b.id, e.target.value)}
                                                    >
                                                        <option value="read">Прочитано</option>
                                                        <option value="CURRENTLY_READING">Читаю</option>
                                                        <option value="WANT_TO_READ">Хочу прочитати</option>
                                                    </select>
                                                </td>
                                                <td>
                                                    <button
                                                        className="action-btn delete-btn"
                                                        onClick={() => deleteFromLibrary(b.id)}
                                                    >
                                                        <i className="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )}

                        {/* -------- pagination з вікном -------- */}
                        {totalPages > 1 && (
                            <nav aria-label="Навігація по сторінках">
                                <ul className="pagination justify-content-center mb-4">

                                    {/* Кнопка "Попередня" */}
                                    <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                                        <button
                                            className="page-link"
                                            onClick={() => setFilter({...filter, page: Math.max(0, filter.page - 1)})}
                                            disabled={currentPage === 0}
                                            aria-label="Попередня сторінка"
                                        >
                                            &laquo;
                                        </button>
                                    </li>

                                    {/* Динамічне вікно сторінок */}
                                    {pageWindow.map((p, idx) =>
                                        p === "left-ellipsis" || p === "right-ellipsis" ? (
                                            <li key={p + idx} className="page-item disabled">
                                                <span className="page-link">…</span>
                                            </li>
                                        ) : (
                                            <li key={p} className={`page-item ${p === currentPage ? "active" : ""}`}>
                                                <button
                                                    className="page-link"
                                                    onClick={() => setFilter({...filter, page: p})}
                                                    aria-label={`Сторінка ${p + 1}`}
                                                    aria-current={p === currentPage ? "page" : undefined}
                                                >
                                                    {p + 1}
                                                </button>
                                            </li>
                                        )
                                    )}

                                    {/* Кнопка "Наступна" */}
                                    <li className={`page-item ${currentPage >= totalPages - 1 ? "disabled" : ""}`}>
                                        <button
                                            className="page-link"
                                            onClick={() => setFilter({
                                                ...filter,
                                                page: Math.min(totalPages - 1, filter.page + 1)
                                            })}
                                            disabled={currentPage >= totalPages - 1}
                                            aria-label="Наступна сторінка"
                                        >
                                            &raquo;
                                        </button>
                                    </li>
                                </ul>
                            </nav>
                        )}
                    </div>

                    {/* -------- filters -------- */}
                    <div className="col-lg-3 order-lg-1">
                        <Filters
                            genres={genres}
                            filter={filter}
                            setFilter={setFilter}
                        />
                    </div>
                </div>
            </div>

            {/* Review Modal */}
            {activeReviewBook && (
                <ReviewModal
                    book={activeReviewBook}
                    onClose={closeReviewModal}
                    onSave={handleReviewSave}
                />
            )}

            <Footer/>
        </>
    );
}

/* ========== helpers ========== */

function Stat({n, label}) {
    return (
        <div className="col-md-3 col-6">
            <div className="stat-item">
                <div className="stat-number">{n}</div>
                <div className="stat-label">{label}</div>
            </div>
        </div>
    );
}

function Stars({score = 0, onChange}) {
    return (
        <div className="rating-stars">
            {Array.from({length: 5}).map((_, i) => (
                <i
                    key={i}
                    className={i < score ? "fas fa-star" : "far fa-star"}
                    style={{cursor: "pointer"}}
                    onClick={() => onChange(i + 1)}
                />
            ))}
        </div>
    );
}

/* --- filters component (пошук, жанри, рейтинг, роки) --- */
function Filters({genres = [], filter, setFilter}) {
    /* локальні стани для form-inputs */
    const [local, setLocal] = useState({
        search: filter.search,
        rating: filter.rating,
        yearFrom: filter.yearFrom,
        yearTo: filter.yearTo,
        genres: new Set(filter.genres),
        pageSize: filter.pageSize
    });

    /* --- helpers --- */
    const toggleGenre = id => {
        const g = new Set(local.genres);
        g.has(id) ? g.delete(id) : g.add(id);
        setLocal({...local, genres: g});
    };

    const apply = () => setFilter({
        ...filter,
        search: local.search.trim(),
        rating: local.rating,
        yearFrom: local.yearFrom ? Number(local.yearFrom) : null,
        yearTo: local.yearTo ? Number(local.yearTo) : null,
        genres: local.genres,
        pageSize: local.pageSize,
        page: 0
    });

    const clear = () => {
        setLocal({search: "", rating: null, yearFrom: null, yearTo: null, genres: new Set()});
        setFilter({...filter, search: "", rating: null, yearFrom: null, yearTo: null, genres: new Set(), page: 0});
    };

    /* ——— відображення ——— */
    return (
        <div className="library-filters">
            <h5 className="filter-heading">Фільтри</h5>

            {/* ---------- пошук ---------- */}
            <div className="filter-section">
                <label className="filter-label">Пошук</label>
                <div className="input-group">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Назва або автор"
                        value={local.search}
                        onChange={e => setLocal({...local, search: e.target.value})}/>
                    <button className="btn btn-outline-secondary" onClick={apply}>
                        <i className="fas fa-search"/>
                    </button>
                </div>
            </div>

            {/* ---------- Жанри (випадаючий мульти-select) ---------- */}
            <div className="filter-section">
                <label className="filter-label d-block mb-1">Жанри</label>

                <div className="dropdown w-100">
                    <button
                        className="btn btn-outline-secondary w-100 dropdown-toggle text-start"
                        type="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false">
                        {local.genres.size === 0
                            ? "Не вибрано"
                            : `Обрано: ${local.genres.size}`}
                    </button>

                    <ul className="dropdown-menu p-2" style={{maxHeight: 260, overflowY: "auto", width: "100%"}}>
                        {genres.map(g => (
                            <li key={g.id}>
                                <label className="form-check w-100 mb-1">
                                    <input
                                        className="form-check-input me-2"
                                        type="checkbox"
                                        checked={local.genres.has(g.id)}
                                        onChange={() => toggleGenre(g.id)}/>
                                    {g.name}
                                </label>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {/* ---------- рейтинг ---------- */}
            <div className="filter-section">
                <label className="filter-label">Рейтинг</label>
                {[5, 4, 3].map(r => (
                    <div className="form-check" key={r}>
                        <input
                            type="radio"
                            className="form-check-input"
                            checked={local.rating === r}
                            onChange={() => setLocal({...local, rating: r})}
                            id={`rat-${r}`}/>
                        <label className="form-check-label" htmlFor={`rat-${r}`}>
                            {[...Array(5)].map((_, i) =>
                                <i key={i}
                                   className={i < r ? "fas fa-star text-warning" : "far fa-star text-warning"}/>)} {r < 5 && "і вище"}
                        </label>
                    </div>
                ))}
                <div className="form-check">
                    <input type="radio" id="rat-any" className="form-check-input"
                           checked={local.rating == null}
                           onChange={() => setLocal({...local, rating: null})}/>
                    <label className="form-check-label" htmlFor="rat-any">Будь-який</label>
                </div>
            </div>

            {/* ---------- рік видання ---------- */}
            <div className="filter-section">
                <label className="filter-label">Рік видання</label>
                <input type="number" className="form-control mb-2"
                       placeholder="від"
                       value={local.yearFrom ?? ""}
                       onChange={e => setLocal({...local, yearFrom: e.target.value || null})}/>
                <input type="number" className="form-control"
                       placeholder="до"
                       value={local.yearTo ?? ""}
                       onChange={e => setLocal({...local, yearTo: e.target.value || null})}/>
            </div>

            <div className="filter-section">
                <label className="filter-label">Книг на сторінці</label>
                <select
                    className="form-select"
                    value={local.pageSize}
                    onChange={e => setLocal({...local, pageSize: Number(e.target.value)})}>
                    {[6, 9, 12, 15, 20].map(n => (
                        <option key={n} value={n}>{n}</option>
                    ))}
                </select>
            </div>

            <button className="btn btn-primary w-100 mb-2" onClick={apply}>Застосувати</button>
            <button className="btn btn-outline-secondary w-100" onClick={clear}>Скинути</button>
        </div>
    );
}