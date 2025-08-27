// src/pages/SeriesPage.jsx
import {useEffect, useState} from "react";
import {Link, useParams} from "react-router-dom";
import {fetchSeriesBooks, fetchSeriesById, fetchSeriesStats} from "../../api/series";
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";
import BookCard from "../BookCard/BookCard";
import "./SeriesPage.css";
import PaginationWindow from "../PaginationWindow/PaginationWindow";

export default function SeriesPage() {
    const {id} = useParams();

    const [series, setSeries] = useState(null);
    const [stats, setStats] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const [books, setBooks] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [booksLoading, setBooksLoading] = useState(false);

    const [page, setPage] = useState(0);
    const [size] = useState(8);
    const [sort, setSort] = useState("seriesNumber,asc");

    useEffect(() => {
        Promise.all([
            fetchSeriesById(id),
            fetchSeriesStats(id)
        ])
            .then(([seriesRes, statsRes]) => {
                setSeries(seriesRes.data);
                setStats(statsRes.data);
            })
            .catch(() => setError("Серія не знайдена"))
            .finally(() => setLoading(false));
    }, [id]);

    useEffect(() => {
        if (!series) return;

        setBooksLoading(true);
        fetchSeriesBooks(id, {page, size, sort})
            .then(res => {
                setBooks(res.data.content);
                setTotalPages(res.data.page.totalPages);
                setTotalElements(res.data.page.totalElements);
            })
            .catch(err => console.error("Помилка завантаження книг:", err))
            .finally(() => setBooksLoading(false));
    }, [id, series, page, size, sort]);


    if (loading) return <div className="container mt-5">Завантаження…</div>;
    if (error) return <div className="container mt-5 text-danger">{error}</div>;

    return (
        <>
            <Navbar/>

            <div className="container mt-4">
                <nav aria-label="breadcrumb">
                    <ol className="breadcrumb">
                        <li className="breadcrumb-item">
                            <Link to={-1} onClick={(e) => {
                                e.preventDefault();
                                window.history.back();
                            }}>
                                Назад
                            </Link>
                        </li>
                        <li className="breadcrumb-item active" aria-current="page">{series.name}</li>
                    </ol>
                </nav>

                <div className="series-container mb-5">
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="series-name mb-3">Серія «{series.name}»</h1>

                            {/* Статистика серії */}
                            <div className="series-stats mb-4">
                                <div className="series-stats-item">
                                    <i className="fas fa-book text-primary"></i>
                                    <span>{stats?.bookCount || 0} книг</span>
                                </div>
                                <div className="series-stats-item">
                                    <i className="fas fa-star text-warning"></i>
                                    <span>{stats?.averageRating ? stats.averageRating.toFixed(1) : "—"} середній рейтинг</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="books-section">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h4>Книги серії</h4>

                        {/* Сортування */}
                        <div className="d-flex align-items-center">
                            <label htmlFor="sort-select" className="form-label me-2 mb-0">Сортувати:</label>
                            <select
                                id="sort-select"
                                className="form-select"
                                style={{width: "auto"}}
                                value={sort}
                                onChange={e => {
                                    setSort(e.target.value);
                                    setPage(0);
                                }}
                            >
                                <option value="seriesNumber,asc">За номером у серії</option>
                                <option value="title,asc">Назва (А-Я)</option>
                                <option value="title,desc">Назва (Я-А)</option>
                                <option value="averageRating,desc">Рейтинг ↓</option>
                                <option value="averageRating,asc">Рейтинг ↑</option>
                                <option value="publicationDate,desc">Нові спочатку</option>
                                <option value="publicationDate,asc">Старі спочатку</option>
                            </select>
                        </div>
                    </div>

                    <p className="mb-4 text-muted">
                        Показано {books.length} з {totalElements} книг
                    </p>

                    {booksLoading && (
                        <div className="text-center py-4">
                            <div className="spinner-border" role="status">
                                <span className="visually-hidden">Завантаження...</span>
                            </div>
                        </div>
                    )}

                    {!booksLoading && (
                        <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4 mb-5">
                            {books.map(book => (
                                <BookCard key={book.id} book={book}/>
                            ))}
                        </div>
                    )}

                    {!booksLoading && books.length === 0 && (
                        <div className="text-center py-5">
                            <i className="fas fa-book-open fa-3x text-muted mb-3"></i>
                            <h4 className="text-muted">Книги не знайдені</h4>
                            <p className="text-muted">У цій серії поки що немає книг в каталозі.</p>
                        </div>
                    )}

                    <PaginationWindow
                        currentPage={page}
                        totalPages={totalPages}
                        onChange={(p) => setPage(p)}
                    />
                </div>
            </div>

            <Footer/>
        </>
    );
}