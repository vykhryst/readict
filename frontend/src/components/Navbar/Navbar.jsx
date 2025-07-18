import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import SearchBar from "../SearchBar/SearchBar";
import "./Navbar.css";

export default function Navbar() {
    const { user, isGuest, logout, loading } = useAuth();

    const renderUserSection = () => {
        if (loading) return null;

        if (isGuest) {
            return (
                <li className="nav-item">
                    <Link className="nav-link active" to="/login">
                        Увійти
                    </Link>
                </li>
            );
        }

        // authenticated user
        return (
            <>
                <li className="nav-item">
                    <Link className="nav-link" to="/recommendations">
                        Рекомендації
                    </Link>
                </li>

                <li className="nav-item">
                    <Link className="nav-link" to="/library">
                        Моя бібліотека
                    </Link>
                </li>

                <li className="nav-item dropdown">
                    <button
                        className="nav-link dropdown-toggle"
                        id="navbarDropdown"
                        data-bs-toggle="dropdown"
                        aria-expanded="false"
                    >
                        <i className="fas fa-user me-1"/>
                        {user.firstName}
                    </button>

                    <ul
                        className="dropdown-menu dropdown-menu-end"
                        aria-labelledby="navbarDropdown"
                    >
                        <li>
                            <Link className="dropdown-item" to="/profile">
                                <i className="fas fa-user me-2"/>Мій профіль
                            </Link>
                        </li>
                        <li>
                            <hr className="dropdown-divider"/>
                        </li>
                        <li>
                            <button className="dropdown-item" onClick={logout}>
                                <i className="fas fa-sign-out-alt me-2"/>Вийти
                            </button>
                        </li>
                    </ul>
                </li>
            </>
        );
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-light sticky-top">
            <div className="container">
                <Link className="navbar-brand" to="/home">
                    <i className="fas fa-book-open me-2"/>Readict
                </Link>

                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarNav"
                    aria-controls="navbarNav"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"/>
                </button>

                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav ms-auto">
                        {/* Пошук */}
                        <li className="nav-item d-flex align-items-center">
                            <SearchBar />
                        </li>

                        {/* Каталог (для всіх) */}
                        <li className="nav-item">
                            <Link className="nav-link" to="/catalog">
                                Каталог книг
                            </Link>
                        </li>

                        {/* Правий блок залежно від стана автентифікації */}
                        {renderUserSection()}
                    </ul>
                </div>
            </div>
        </nav>
    );
}