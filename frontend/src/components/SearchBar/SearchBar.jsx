// src/components/SearchBar/SearchBar.jsx
import { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { fetchBooks } from "../../api/books";
import "./SearchBar.css";

export default function SearchBar() {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [isOpen, setIsOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const searchRef = useRef(null);
    const navigate = useNavigate();

    // Закриваємо випадаючий список при кліку поза ним
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (searchRef.current && !searchRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    // Пошук книг з затримкою (debounce)
    useEffect(() => {
        if (!query.trim()) {
            setResults([]);
            setIsOpen(false);
            return;
        }

        const timer = setTimeout(() => {
            setLoading(true);
            fetchBooks({
                title: query,
                genreIds: [],
                page: 0,
                size: 6, // Отримуємо 6 результатів (5 показуємо + перевіряємо чи є ще)
                sort: "title,asc"
            })
                .then(response => {
                    const books = response.data.content || [];
                    setResults(books);
                    setIsOpen(books.length > 0);
                })
                .catch(error => {
                    console.error("Search error:", error);
                    setResults([]);
                    setIsOpen(false);
                })
                .finally(() => {
                    setLoading(false);
                });
        }, 300); // Затримка 300мс

        return () => clearTimeout(timer);
    }, [query]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (query.trim()) {
            navigate(`/catalog?title=${encodeURIComponent(query.trim())}`);
            setIsOpen(false);
            setQuery("");
        }
    };

    const handleInputChange = (e) => {
        setQuery(e.target.value);
    };

    const handleBookClick = () => {
        setIsOpen(false);
        setQuery("");
    };

    const displayedResults = results.slice(0, 5);
    const hasMoreResults = results.length > 5;

    return (
        <div className="search-bar" ref={searchRef}>
            <form className="d-flex search-form me-2" onSubmit={handleSubmit}>
                <div className="search-input-container">
                    <input
                        className="form-control search-input"
                        type="search"
                        placeholder="Пошук книг..."
                        aria-label="Search"
                        value={query}
                        onChange={handleInputChange}
                        onFocus={() => {
                            if (results.length > 0) setIsOpen(true);
                        }}
                    />

                    {/* Випадаючий список результатів */}
                    {isOpen && (
                        <div className="search-dropdown">
                            {loading && (
                                <div className="search-loading">
                                    <i className="fas fa-spinner fa-spin me-2"></i>
                                    Пошук...
                                </div>
                            )}

                            {!loading && displayedResults.length > 0 && (
                                <>
                                    {displayedResults.map(book => (
                                        <Link
                                            key={book.id}
                                            to={`/books/${book.id}`}
                                            className="search-result-item"
                                            onClick={handleBookClick}
                                        >
                                            <img
                                                src={book.cover || "/placeholder-book.jpg"}
                                                alt={book.title}
                                                className="search-result-cover"
                                                onError={(e) => {
                                                    e.target.src = "/placeholder-book.jpg";
                                                }}
                                            />
                                            <div className="search-result-info">
                                                <div className="search-result-title">
                                                    {book.title}
                                                </div>
                                                <div className="search-result-author">
                                                    {book.author?.firstName && book.author?.lastName
                                                        ? `${book.author.firstName} ${book.author.lastName}`
                                                        : "Невідомий автор"
                                                    }
                                                </div>
                                            </div>
                                        </Link>
                                    ))}

                                    {hasMoreResults && (
                                        <Link
                                            to={`/catalog?title=${encodeURIComponent(query.trim())}`}
                                            className="search-show-all"
                                            onClick={handleBookClick}
                                        >
                                            <i className="fas fa-search me-2"></i>
                                            Переглянути всі результати
                                        </Link>
                                    )}
                                </>
                            )}

                            {!loading && displayedResults.length === 0 && query.trim() && (
                                <div className="search-no-results">
                                    <i className="fas fa-search me-2"></i>
                                    Нічого не знайдено
                                </div>
                            )}
                        </div>
                    )}
                </div>

                <button className="btn btn-light search-button" type="submit">
                    <i className="fas fa-search"></i>
                </button>
            </form>
        </div>
    );
}