import {useEffect, useState} from "react";
import {changePwd, getMe, updateMe} from "../../api/user";
import {fetchGenres} from "../../api/books";
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";
import "./ProfilePage.css";
import classNames from "classnames";

export default function ProfilePage() {

    /* ---------- state ---------- */
    const [me, setMe] = useState(null);        // дані з бек-енду
    const [genres, setGenres] = useState([]);  // усі жанри
    const [form, setForm] = useState(null);    // editable copy
    const [pwd, setPwd] = useState({cur: "", next: "", rep: ""});

    const [busy, setBusy] = useState(false);   // кнопки «мерехтять»
    const [msg, setMsg] = useState(null);      // короткі повідомлення

    /* ---------- initial fetch ---------- */
    useEffect(() => {
        Promise.all([getMe(), fetchGenres()])
            .then(([u, g]) => {
                setMe(u.data);
                setForm({
                    firstName: u.data.firstName,
                    lastName: u.data.lastName,
                    favourite: new Set(u.data.favouriteGenreIds)
                });
                const list = Array.isArray(g.data) ? g.data : (g.data?.content ?? []);
                setGenres(list);
            });
    }, []);

    /* ---------- helpers ---------- */
    const toggleGenre = id => {
        const fav = new Set(form.favourite);
        fav.has(id) ? fav.delete(id) : fav.add(id);
        setForm({...form, favourite: fav});
    };

    /* ---- особисті дані + жанри ---- */
    const savePersonal = () => {
        setBusy(true);
        updateMe({
            firstName: form.firstName.trim(),
            lastName: form.lastName.trim(),
            favouriteGenreIds: Array.from(form.favourite)
        })
            .then(r => {
                setMsg("✅ Збережено");
                setMe(r.data ?? {
                    ...me,
                    firstName: form.firstName.trim(),
                    lastName: form.lastName.trim(),
                    favouriteGenreIds: Array.from(form.favourite)
                });
            })
            .finally(() => setBusy(false));
    };

    const savePwd = () => {
        if (pwd.next.length < 8) return setMsg("❗ Пароль ≥ 8 символів");
        if (pwd.next !== pwd.rep) return setMsg("❗ Паролі не збігаються");

        setBusy(true);
        changePwd({currentPassword: pwd.cur, newPassword: pwd.next})
            .then(() => {
                setMsg("✅ Пароль змінено");
                setPwd({cur: "", next: "", rep: ""});
            })
            .catch(e => setMsg(e.response?.data?.error || "Помилка"))
            .finally(() => setBusy(false));
    };

    const renderFavouriteGenres = () => {
        if (!form || form.favourite.size === 0) {
            return <span className="empty-genres-message">Не вибрано жодного жанру</span>;
        }

        return (
            <>
                {Array.from(form.favourite).map(id => {
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

    if (!form) return null;

    return (
        <>
            <Navbar/>

            <div className="container mt-4">
                <div className="profile-container">
                    <h2 className="page-title">Профіль користувача</h2>

                    {/* —— system messages —— */}
                    {msg && (
                        <div className="alert alert-info py-2 px-3 mb-3"
                             onAnimationEnd={() => setMsg(null)}>
                            {msg}
                        </div>
                    )}

                    {/* ------- TABS ------- */}
                    <ul className="nav nav-tabs profile-tabs" role="tablist">
                        <li className="nav-item" role="presentation">
                            <button className="nav-link active"
                                    id="personal-tab"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-personal"
                                    type="button"
                                    role="tab"
                                    aria-controls="tab-personal"
                                    aria-selected="true">
                                Особисті&nbsp;дані
                            </button>
                        </li>
                        <li className="nav-item" role="presentation">
                            <button className="nav-link"
                                    id="password-tab"
                                    data-bs-toggle="tab"
                                    data-bs-target="#tab-password"
                                    type="button"
                                    role="tab"
                                    aria-controls="tab-password"
                                    aria-selected="false">
                                Зміна&nbsp;паролю
                            </button>
                        </li>
                    </ul>

                    <div className="tab-content" id="profileTabsContent">

                        {/* ---------- PERSONAL ---------- */}
                        <div className="tab-pane fade show active" id="tab-personal" role="tabpanel"
                             aria-labelledby="personal-tab">

                            <div className="row mt-3 gx-3">
                                <div className="col-md-6 mb-3">
                                    <label htmlFor="firstName" className="form-label">Ім'я</label>
                                    <input type="text"
                                           className="form-control"
                                           id="firstName"
                                           value={form.firstName}
                                           onChange={e => setForm({...form, firstName: e.target.value})}/>
                                </div>
                                <div className="col-md-6 mb-3">
                                    <label htmlFor="lastName" className="form-label">Прізвище</label>
                                    <input type="text"
                                           className="form-control"
                                           id="lastName"
                                           value={form.lastName}
                                           onChange={e => setForm({...form, lastName: e.target.value})}/>
                                </div>
                            </div>

                            <div className="mb-3">
                                <label htmlFor="email" className="form-label">Е-mail (незмінюваний)</label>
                                <input type="email"
                                       className="form-control"
                                       id="email"
                                       value={me.email}
                                       disabled/>
                            </div>

                            {/* ---------- favourite genres ---------- */}
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
                                        {form.favourite.size > 0 &&
                                            <span className="genres-count-badge">
                                                {form.favourite.size}
                                            </span>
                                        }
                                    </button>
                                    <ul className="dropdown-menu genres-menu" aria-labelledby="genresDropdown">
                                        {genres.map(genre => (
                                            <li key={genre.id}>
                                                <label className="genre-checkbox-label w-100">
                                                    <input type="checkbox"
                                                           className="genre-checkbox-input"
                                                           checked={form.favourite.has(genre.id)}
                                                           onChange={() => toggleGenre(genre.id)}/>
                                                    {genre.name}
                                                </label>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            </div>

                            <div className="mt-3">
                                <button className={classNames("btn btn-primary", {disabled: busy})}
                                        onClick={savePersonal}>
                                    Зберегти зміни
                                </button>
                                <button type="reset"
                                        className="btn btn-outline-secondary ms-2"
                                        onClick={() => setForm({
                                            firstName: me.firstName,
                                            lastName: me.lastName,
                                            favourite: new Set(me.favouriteGenreIds || [])
                                        })}>
                                    Скасувати
                                </button>
                            </div>
                        </div>

                        {/* ---------- PASSWORD ---------- */}
                        <div className="tab-pane fade" id="tab-password" role="tabpanel" aria-labelledby="password-tab">

                            <div className="row mt-3 gx-3">
                                <div className="col-md-6 mb-3">
                                    <label htmlFor="currentPassword" className="form-label">Поточний пароль</label>
                                    <input type="password"
                                           className="form-control"
                                           id="currentPassword"
                                           value={pwd.cur}
                                           onChange={e => setPwd({...pwd, cur: e.target.value})}/>
                                </div>
                            </div>

                            <div className="row gx-3">
                                <div className="col-md-6 mb-3">
                                    <label htmlFor="newPassword" className="form-label">Новий пароль</label>
                                    <input type="password"
                                           className="form-control"
                                           id="newPassword"
                                           value={pwd.next}
                                           onChange={e => setPwd({...pwd, next: e.target.value})}/>
                                    <div className="password-requirements">
                                        <p className="mb-1">Пароль повинен містити:</p>
                                        <ul>
                                            <li>Мінімум 8 символів</li>
                                        </ul>
                                    </div>
                                </div>
                                <div className="col-md-6 mb-3">
                                    <label htmlFor="confirmPassword" className="form-label">Підтвердження нового
                                        паролю</label>
                                    <input type="password"
                                           className="form-control"
                                           id="confirmPassword"
                                           value={pwd.rep}
                                           onChange={e => setPwd({...pwd, rep: e.target.value})}/>
                                </div>
                            </div>

                            <div className="mt-3">
                                <button className={classNames("btn btn-primary", {disabled: busy})}
                                        onClick={savePwd}>
                                    Змінити пароль
                                </button>
                                <button type="reset"
                                        className="btn btn-outline-secondary ms-2"
                                        onClick={() => setPwd({cur: "", next: "", rep: ""})}>
                                    Скасувати
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Footer/>
        </>
    );
}