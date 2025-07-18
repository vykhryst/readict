import {useEffect, useMemo, useState} from "react";
import classNames from "classnames";

import {getRecommendations} from "../../api/recommendations";
import {getShelf, removeBook, setShelf} from "../../api/library";
import {fetchGenres} from "../../api/books";

import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";

import "./RecommendationsPage.css";
import {Link} from "react-router-dom";

export default function RecommendationsPage() {
    /* ---------- state ---------- */
    const [genres, setGenres] = useState([]);
    const [filter, setFilter] = useState({
        genreId: null,
        sort: null,
        size: 6,
        page: 0,
    });
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    // shelfMap[bookId] = "READ" | "CURRENTLY_READING" | "WANT_TO_READ" | null
    const [shelfMap, setShelfMap] = useState({});

    /* ---------- initial: genres ---------- */
    useEffect(() => {
        fetchGenres().then(r => {
            const list = Array.isArray(r.data) ? r.data : r.data?.content ?? [];
            setGenres(list);
        });
    }, []);

    /* ---------- load recs whenever filter changes ---------- */
    useEffect(() => {
        setLoading(true);
        getRecommendations({
            genreId: filter.genreId ?? undefined,
            sort: filter.sort ?? undefined,
            page: filter.page,
            size: filter.size,
        })
            .then(r => setData(r.data))
            .finally(() => setLoading(false));
    }, [filter]);

    /* ---------- load shelf status for each book ---------- */
    useEffect(() => {
        if (!data) return;
        data.content.forEach(b => {
            getShelf(b.id)
                .then(r => r.status === 200 && setShelfMap(m => ({...m, [b.id]: r.data})))
                .catch(() => {
                }); // if 204 No Content → leave undefined
        });
    }, [data]);

    /* ---------- helpers ---------- */
    const sortingOptions = [
        {value: null, label: "За замовчуванням"},
        {value: "title,asc", label: "Назва (А-Я)"},
        {value: "title,desc", label: "Назва (Я-А)"},
        {value: "averageRating,desc", label: "Рейтинг (спадання)"},
        {value: "averageRating,asc", label: "Рейтинг (зростання)"},
        {value: "publicationDate,desc", label: "Публікація (нові)"},
        {value: "publicationDate,asc", label: "Публікація (старі)"},
    ];

    const shelves = [
        {code: "WANT_TO_READ", label: "Хочу прочитати", icon: "fas fa-bookmark"},
        {code: "CURRENTLY_READING", label: "Читаю", icon: "fas fa-book-open"},
        {code: "READ", label: "Прочитано", icon: "fas fa-check"},
    ];

    const handleMove = (bookId, code) =>
        setShelf(bookId, code).then(() => {
            // оновимо статус
            setShelfMap(m => ({...m, [bookId]: code}));

        });

    const handleRemove = bookId =>
        removeBook(bookId).then(() => {
            const {[bookId]: _, ...rest} = shelfMap;
            setShelfMap(rest);
        });

    const handleSkip = bookId =>
        setData(prev => ({
            ...prev,
            content: prev.content.filter(b => b.id !== bookId)
        }));


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

    /* ---------- pagination meta ---------- */
    const meta = data?.page ?? {number: 0, totalPages: 0, totalElements: 0};
    const currentPage = meta.number;
    const totalPages = meta.totalPages;

    // Мемоізоване вікно пагінації
    const pageWindow = useMemo(
        () => getPageWindow(currentPage, totalPages),
        [currentPage, totalPages]
    );

    /* ---------- UI ---------- */
    return (
        <>
            <Navbar/>

            {/* hero banner */}
            <header className="hero-small text-center">
                <div className="container">
                    <h2 className="mb-2">Персональні рекомендації</h2>
                    <p className="lead">
                        Книги, дібрані спеціально для вас на основі вашої бібліотеки та вподобань.
                    </p>
                </div>
            </header>

            <div className="container">
                <div className="row">

                    {/* ========== FILTERS ========== */}
                    <div className="col-lg-3">
                        {/* Сортування */}
                        <div className="card filter-card mb-3">
                            <div className="card-header">Сортування</div>
                            <div className="card-body">
                                <select
                                    className="form-select"
                                    value={filter.sort ?? ""}
                                    onChange={e => setFilter({...filter, sort: e.target.value || null, page: 0})}
                                >
                                    {sortingOptions.map(o => (
                                        <option key={o.value ?? "_"} value={o.value ?? ""}>
                                            {o.label}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Жанри */}
                        <div className="card filter-card mb-3">
                            <div className="card-header">Жанри</div>
                            <div className="card-body">
                                <div className="dropdown w-100">
                                    <button className="btn btn-outline-secondary dropdown-toggle w-100"
                                            data-bs-toggle="dropdown">
                                        {filter.genreId
                                            ? genres.find(g => g.id === filter.genreId)?.name
                                            : "Всі жанри"}
                                    </button>
                                    <ul className="dropdown-menu dropdown-menu-scrollable w-100">
                                        <li>
                                            <button
                                                className={classNames("dropdown-item", {active: filter.genreId == null})}
                                                onClick={() => setFilter({...filter, genreId: null, page: 0})}>
                                                Всі жанри
                                            </button>
                                        </li>
                                        <li>
                                            <hr className="dropdown-divider"/>
                                        </li>
                                        {genres.map(g => (
                                            <li key={g.id}>
                                                <button
                                                    className={classNames("dropdown-item", {active: filter.genreId === g.id})}
                                                    onClick={() => setFilter({...filter, genreId: g.id, page: 0})}>
                                                    {g.name}
                                                </button>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>

                        {/* Кількість на сторінці */}
                        <div className="card filter-card">
                            <div className="card-header">Кількість на сторінці</div>
                            <div className="card-body">
                                <select className="form-select"
                                        value={filter.size}
                                        onChange={e => setFilter({...filter, size: +e.target.value, page: 0})}>
                                    {[6, 9, 12, 18, 24].map(n => (
                                        <option key={n} value={n}>{n}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>

                    {/* ========== GRID ========== */}
                    <div className="col-lg-9">


                        <div className="d-flex align-items-center mb-4">
                            <p className="m-0">
                                Знайдено {meta.totalElements} рекомендацій
                            </p>
                            <span className="text-muted small ms-auto">
                                    <i className="fas fa-lightbulb me-1"></i>
                                    Оцінюйте прочитані книги — ми підбиратимемо точніше
                                </span>
                        </div>

                        {loading && <p>Завантаження…</p>}

                        {!loading && data && data.content.length === 0 && (
                            <p className="text-muted">Рекомендацій не знайдено.</p>
                        )}

                        {!loading && data && data.content.length > 0 && (
                            <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
                                {data.content.map(b => (
                                    <div className="col" key={b.id}>
                                        <Link to={`/books/${b.id}`} className="text-decoration-none">
                                            <div className="card book-card h-100">

                                                <div className="book-img-container">
                                                    <img src={b.cover}
                                                         alt={b.title}
                                                         className="book-img"/>
                                                </div>
                                                <div className="card-body d-flex flex-column">
                                                    <h5 className="card-title">{b.title}</h5>
                                                    <p className="card-author">
                                                        <Link to={`/authors/${b.author.id}`} className="author-link">
                                                            {b.author.firstName} {b.author.lastName}
                                                        </Link>
                                                    </p>
                                                    <div className="rating mb-2">
                                                        {Array(5).fill().map((_, i) => (
                                                            <i key={i}
                                                               className={i + 1 <= Math.round(b.averageRating)
                                                                   ? "fas fa-star"
                                                                   : "far fa-star"}/>
                                                        ))}
                                                        <span
                                                            className="text-dark ms-1">{b.averageRating.toFixed(1)}</span>
                                                    </div>

                                                    {/* dropdown зі статусом */}
                                                    <div className="dropdown mb-2">
                                                        <button
                                                            className="btn btn-primary dropdown-toggle w-100"
                                                            data-bs-toggle="dropdown"
                                                        >
                                                            {shelves.find(s => s.code === shelfMap[b.id])?.label
                                                                || "Додати до бібліотеки"}
                                                        </button>
                                                        <ul className="dropdown-menu w-100">
                                                            {shelves.map(s => (
                                                                <li key={s.code}>
                                                                    <button className={classNames("dropdown-item", {
                                                                        active: shelfMap[b.id] === s.code
                                                                    })}
                                                                            onClick={() => handleMove(b.id, s.code)}>
                                                                        <i className={`${s.icon} me-2`}/>
                                                                        {s.label}
                                                                    </button>
                                                                </li>
                                                            ))}
                                                            {shelfMap[b.id] && (
                                                                <>
                                                                    <li>
                                                                        <hr className="dropdown-divider"/>
                                                                    </li>
                                                                    <li>
                                                                        <button className="dropdown-item text-danger"
                                                                                onClick={() => handleRemove(b.id)}>
                                                                            <i className="fas fa-times me-2"/>Видалити з
                                                                            бібліотеки
                                                                        </button>
                                                                    </li>
                                                                </>
                                                            )}
                                                        </ul>
                                                    </div>

                                                    {/* кнопка “Не цікавить” */}
                                                    <button
                                                        className="btn btn-outline-secondary"
                                                        onClick={() => handleSkip(b.id)}
                                                    >
                                                        Не цікавить
                                                    </button>
                                                </div>
                                            </div>
                                        </Link>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* pagination з вікном */}
                        {totalPages > 1 && (
                            <nav aria-label="Навігація по сторінках">
                                <ul className="pagination justify-content-center">

                                    {/* Кнопка "Попередня" */}
                                    <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                                        <button
                                            className="page-link"
                                            onClick={() => setFilter(f => ({...f, page: Math.max(0, f.page - 1)}))}
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
                                                    onClick={() => setFilter(f => ({...f, page: p}))}
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
                                            onClick={() => setFilter(f => ({
                                                ...f,
                                                page: Math.min(totalPages - 1, f.page + 1)
                                            }))}
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
                </div>
            </div>

            <Footer/>
        </>
    );
}
