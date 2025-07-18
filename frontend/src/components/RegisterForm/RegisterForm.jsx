import React, {useState, useEffect} from 'react';
import {register} from '../../api/auth';
import {fetchGenres} from '../../api/books';
import {Link, useNavigate} from 'react-router-dom';
import './RegisterForm.css';
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";

export default function RegisterForm() {
    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirm: '',
        agree: false,
        favouriteGenreIds: new Set()
    });
    const [genres, setGenres] = useState([]);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();

    // Fetch genres when component mounts
    useEffect(() => {
        fetchGenres()
            .then((g) => {
                const list = Array.isArray(g.data) ? g.data : (g.data?.content ?? []);
                setGenres(list);
            })
            .catch(err => {
                console.error("Failed to fetch genres:", err);
            });
    }, []);

    const handleChange = e => {
        const {id, value, type, checked} = e.target;
        setForm(f => ({...f, [id]: type === 'checkbox' ? checked : value}));
    };

    const toggleGenre = id => {
        const favouriteGenreIds = new Set(form.favouriteGenreIds);
        favouriteGenreIds.has(id) ? favouriteGenreIds.delete(id) : favouriteGenreIds.add(id);
        setForm({...form, favouriteGenreIds});
    };

    // Render favourite genres
    const renderFavouriteGenres = () => {
        if (form.favouriteGenreIds.size === 0) {
            return <span className="empty-genres-message">Не вибрано жодного жанру</span>;
        }

        return (
            <>
                {Array.from(form.favouriteGenreIds).map(id => {
                    const genre = genres.find(g => g.id === id);
                    if (!genre) return null;
                    return (
                        <span key={id} className="genre-badge">
                            {genre.name}
                        </span>
                    );
                })}
            </>
        );
    };

    const handleSubmit = async e => {
        e.preventDefault();
        if (form.password !== form.confirm) {
            setError('Паролі не співпадають');
            return;
        }

        // Check if at least one genre is selected
        if (form.favouriteGenreIds.size === 0) {
            setError('Виберіть хоча б один улюблений жанр');
            return;
        }

        try {
            setIsSubmitting(true);
            setError(null);
            await register({
                email: form.email,
                firstName: form.firstName,
                lastName: form.lastName,
                password: form.password,
                favouriteGenreIds: Array.from(form.favouriteGenreIds)
            });
            // Redirect directly to login page after successful registration
            navigate('/login', {
                state: {
                    message: 'Реєстрація успішна! Увійдіть з вашими новими обліковими даними.',
                    email: form.email
                }
            });
        } catch (err) {
            setError(err.response?.status === 409
                ? 'Email вже використовується'
                : 'Помилка сервера, спробуйте пізніше');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
            <Navbar/>

            <div className="container">
                <div className="register-container">
                    <div className="register-heading">
                        <h2>Реєстрація</h2>
                    </div>
                    {error && <div className="alert alert-danger">{error}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="row">
                            <div className="col-md-6">
                                <div className="form-floating mb-3">
                                    <input id="firstName" type="text" className="form-control"
                                           placeholder="Ім'я" value={form.firstName}
                                           onChange={handleChange} required/>
                                    <label htmlFor="firstName">Ім'я</label>
                                </div>
                            </div>
                            <div className="col-md-6">
                                <div className="form-floating mb-3">
                                    <input id="lastName" type="text" className="form-control"
                                           placeholder="Прізвище" value={form.lastName}
                                           onChange={handleChange} required/>
                                    <label htmlFor="lastName">Прізвище</label>
                                </div>
                            </div>
                        </div>
                        <div className="form-floating mb-3">
                            <input id="email" type="email" className="form-control"
                                   placeholder="Електронна пошта" value={form.email}
                                   onChange={handleChange} required/>
                            <label htmlFor="email">Електронна пошта</label>
                            <div className="form-text">Ми ніколи не поширюємо вашу електронну адресу</div>
                        </div>
                        <div className="form-floating mb-3">
                            <input id="password" type="password" className="form-control"
                                   placeholder="Пароль" value={form.password}
                                   onChange={handleChange} required/>
                            <label htmlFor="password">Пароль</label>
                            <div className="password-requirements">
                                <p className="mb-1">Пароль повинен містити:</p>
                                <ul>
                                    <li>Мінімум 8 символів</li>
                                    <li>Хоча б одну велику літеру</li>
                                    <li>Хоча б одну малу літеру</li>
                                    <li>Хоча б одну цифру</li>
                                </ul>
                            </div>
                        </div>
                        <div className="form-floating mb-4">
                            <input id="confirm" type="password" className="form-control"
                                   placeholder="Підтвердження паролю" value={form.confirm}
                                   onChange={handleChange} required/>
                            <label htmlFor="confirm">Підтвердження паролю</label>
                        </div>

                        {/* Улюблені жанри */}
                        <div className="mb-4">
                            <label className="form-label d-block mb-2">Улюблені жанри</label>

                            {/* Відображення улюблених жанрів */}
                            <div className="genres-display-area">
                                {renderFavouriteGenres()}
                            </div>

                            {/* Випадаючий список для вибору жанрів */}
                            <div className="dropdown w-100">
                                <button className="btn btn-outline-secondary dropdown-toggle w-100"
                                        type="button"
                                        id="genresDropdown"
                                        data-bs-toggle="dropdown"
                                        data-bs-auto-close="outside"
                                        aria-expanded="false">
                                    Вибрати жанри
                                    {form.favouriteGenreIds.size > 0 &&
                                        <span className="genres-count-badge">
                                            {form.favouriteGenreIds.size}
                                        </span>
                                    }
                                </button>
                                <ul className="dropdown-menu genres-menu" aria-labelledby="genresDropdown">
                                    {genres.map(genre => (
                                        <li key={genre.id}>
                                            <label className="genre-checkbox-label w-100">
                                                <input type="checkbox"
                                                       className="genre-checkbox-input"
                                                       checked={form.favouriteGenreIds.has(genre.id)}
                                                       onChange={() => toggleGenre(genre.id)} />
                                                {genre.name}
                                            </label>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                            <div className="form-text">Виберіть хоча б один жанр, який вас цікавить</div>
                        </div>

                        <div className="form-check mb-4">
                            <input id="agree" className="form-check-input" type="checkbox"
                                   checked={form.agree} onChange={handleChange} required/>
                            <label className="form-check-label" htmlFor="agree">
                                Я погоджуюся з <a href="#" className="text-decoration-none">Умовами користування</a> та <a
                                href="#" className="text-decoration-none">Політикою конфіденційності</a>
                            </label>
                        </div>
                        <div className="d-grid">
                            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                                {isSubmitting ? 'Обробка...' : 'Зареєструватися'}
                            </button>
                        </div>
                    </form>
                    <div className="register-separator">або</div>
                    <div className="social-register">
                        <a href="#" className="social-btn social-google"><i className="fab fa-google"/></a>
                        <a href="#" className="social-btn social-facebook"><i className="fab fa-facebook-f"/></a>
                        <a href="#" className="social-btn social-apple"><i className="fab fa-apple"/></a>
                    </div>
                    <div className="login-prompt">
                        Вже маєте обліковий запис? <Link to="/login">Увійти</Link>
                    </div>
                </div>
            </div>
            <Footer/>
        </>
    );
}