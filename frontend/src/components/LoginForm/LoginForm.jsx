import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './LoginForm.css';
import Navbar from "../Navbar/Navbar";
import Footer from "../Footer/Footer";
import {useAuth} from "../../context/AuthContext";


export default function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    const {login} = useAuth();                  // ← беремо метод контексту
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(email, password);             // ← оновить контекст + cookie
            navigate("/home");                            // головна / profile
        } catch (err) {
            if (err.response?.status === 401) setError("Невірний email або пароль");
            else setError("Сталася помилка. Спробуйте ще раз");
        }
    };


    return (
        <>
            <Navbar/>

            <div className="container">
                <div className="row">
                    <div className="col-md-10 col-lg-8 mx-auto">
                        <div className="login-container">
                            <div className="login-heading">
                                <h2>Увійти</h2>
                            </div>
                            {error && <div className="alert alert-danger">{error}</div>}
                            <form onSubmit={handleSubmit}>
                                <div className="form-floating mb-3">
                                    <input
                                        type="email"
                                        className="form-control"
                                        id="floatingEmail"
                                        placeholder="name@example.com"
                                        value={email}
                                        onChange={e => setEmail(e.target.value)}
                                        required
                                    />
                                    <label htmlFor="floatingEmail">Електронна пошта</label>
                                </div>
                                <div className="form-floating mb-3">
                                    <input
                                        type="password"
                                        className="form-control"
                                        id="floatingPassword"
                                        placeholder="Пароль"
                                        value={password}
                                        onChange={e => setPassword(e.target.value)}
                                        required
                                    />
                                    <label htmlFor="floatingPassword">Пароль</label>
                                </div>
                                <div className="d-flex justify-content-between align-items-center mb-4">
                                    <div className="form-check">
                                        <input className="form-check-input" type="checkbox" id="rememberCheck"/>
                                        <label className="form-check-label" htmlFor="rememberCheck">
                                            Запам'ятати мене
                                        </label>
                                    </div>
                                    <Link to="/forgot-password" className="text-decoration-none">
                                        Забули пароль?
                                    </Link>
                                </div>
                                <button type="submit" className="btn btn-primary w-100">
                                    Увійти
                                </button>
                            </form>

                            <div className="login-separator">або</div>
                            <div className="social-login">
                                <a href="#" className="social-btn social-google"><i className="fab fa-google"/></a>
                                <a href="#" className="social-btn social-facebook"><i
                                    className="fab fa-facebook-f"/></a>
                                <a href="#" className="social-btn social-apple"><i className="fab fa-apple"/></a>
                            </div>
                            <div className="register-prompt">
                                Ще не маєте аккаунту? <Link to="/register">Зареєструватися</Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Footer/>
        </>
    );
}
