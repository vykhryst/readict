import {useEffect, useState} from "react";
import {fetchBooks, fetchGenres} from "../../api/books";
import "./CatalogPage.css";
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";
import BookCard from "../BookCard/BookCard";
import PaginationWindow from "../PaginationWindow/PaginationWindow";

export default function CatalogPage() {
    const [title, setTitle] = useState("");
    const [genreIds, setGenreIds] = useState([]);
    const [sort, setSort] = useState("averageRating,desc");
    const [size, setSize] = useState(6);
    const [page, setPage] = useState(0);

    const [books, setBooks] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [genres, setGenres] = useState([]);

    useEffect(() => {
        fetchGenres().then(res => setGenres(res.data.content));
    }, []);

    useEffect(() => {
        fetchBooks({title, genreIds, page, size, sort})
            .then(res => {
                setBooks(res.data.content);
                setTotalPages(res.data.page.totalPages);
                setTotalElements(res.data.page.totalElements);
            });
    }, [title, genreIds, sort, size, page]);


    const toggleGenre = id =>
        setGenreIds(g =>
            g.includes(id) ? g.filter(i => i !== id) : [...g, id]);

    const resetFilters = () => {
        setTitle("");
        setGenreIds([]);
        setSort("averageRating,desc");
        setSize(6);
        setPage(0);
    };

    return (
        <>
            <Navbar/>

            {/* hero-banner */}
            <header className="hero-small text-center">
                <div className="container">
                    <h2 className="mb-2">Каталог книг</h2>
                    <p className="lead">Знайдіть свою наступну улюблену книгу</p>
                </div>
            </header>

            <div className="container">
                <div className="row">
                    <aside className="col-lg-3">
                        {/* Пошук */}
                        <div className="card filter-card">
                            <div className="card-header">Пошук</div>
                            <div className="card-body">
                                <input className="form-control mb-2"
                                       value={title}
                                       onChange={e => setTitle(e.target.value)}
                                       placeholder="Назва або автор"/>
                                <button className="btn btn-primary w-100"
                                        onClick={() => setPage(0)}>
                                    Знайти
                                </button>
                            </div>
                        </div>

                        <div className="card filter-card">
                            <div className="card-header">Сортування</div>
                            <div className="card-body">
                                <select className="form-select"
                                        value={sort}
                                        onChange={e => setSort(e.target.value)}>
                                    <option value="title,asc">Назва (А-Я)</option>
                                    <option value="title,desc">Назва (Я-А)</option>
                                    <option value="averageRating,desc">Рейтинг ↓</option>
                                    <option value="averageRating,asc">Рейтинг ↑</option>
                                    <option value="publicationDate,desc">Нові спочатку</option>
                                    <option value="publicationDate,asc">Старі спочатку</option>
                                </select>
                            </div>
                        </div>

                        <div className="card filter-card">
                            <div className="card-header">Жанри</div>
                            <div className="card-body">
                                <div className="dropdown">
                                    <button
                                        className="btn btn-outline-secondary dropdown-toggle w-100"
                                        data-bs-toggle="dropdown"
                                        data-bs-auto-close="outside"
                                        type="button"
                                    >
                                        Вибрати жанри
                                    </button>

                                    <ul className="dropdown-menu dropdown-menu-scrollable w-100">
                                        {genres.map(g => (
                                            <li key={g.id} className="px-3 py-1">
                                                <div className="form-check">
                                                    <input className="form-check-input"
                                                           type="checkbox"
                                                           id={`genre-${g.id}`}
                                                           checked={genreIds.includes(g.id)}
                                                           onChange={() => toggleGenre(g.id)}/>
                                                    <label className="form-check-label" htmlFor={`genre-${g.id}`}>
                                                        {g.name}
                                                    </label>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>
                        </div>

                        <div className="card filter-card">
                            <div className="card-header">Кількість на сторінці</div>
                            <div className="card-body">
                                <select className="form-select"
                                        value={size}
                                        onChange={e => {
                                            setSize(+e.target.value);
                                            setPage(0);
                                        }}>
                                    {[6, 9, 12, 18, 24].map(v =>
                                        <option key={v} value={v}>{v}</option>)}
                                </select>
                            </div>
                        </div>

                        <button className="btn btn-outline-secondary w-100 mb-4"
                                onClick={resetFilters}>
                            Скинути фільтри
                        </button>
                    </aside>

                    <section className="col-lg-9">
                        {/* підзаголовок */}
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <p className="m-0">
                                Знайдено {totalElements} книг
                            </p>
                        </div>

                        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 mb-4">
                            {books.map(b => <BookCard key={b.id} book={b}/>)}
                        </div>

                        <PaginationWindow
                            currentPage={page}
                            totalPages={totalPages}
                            onChange={(p) => setPage(p)}
                        />
                    </section>
                </div>
            </div>

            <Footer/>
        </>
    );
}
