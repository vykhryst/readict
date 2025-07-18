import React from 'react';
import {Link} from 'react-router-dom';
import Footer from "../Footer/Footer";
import Navbar from "../Navbar/Navbar";
import './HomePage.css';


function HomePage() {
    return (
        <>
            <Navbar/>
            {/* Головний банер (центрований) */}
            <header className="hero text-center">
                <div className="container">
                    <div className="row justify-content-center">
                        <div className="col-md-8">
                            <h1 className="mb-3">Ваша особиста книжкова бібліотека</h1>
                            <p className="lead mb-4">Ведіть облік прочитаного, знаходьте нові книги та діліться своїми
                                враженнями з іншими книголюбами.</p>
                            <Link to="/register" className="btn btn-primary me-2">Почати зараз</Link>
                            <Link to="/catalog" className="btn btn-outline-secondary">Дізнатися більше</Link>
                        </div>
                    </div>
                </div>
            </header>

            {/* Основні переваги (центровані) */}
            <section className="py-4">
                <div className="container">
                    <h2 className="text-center mb-4">Основні можливості</h2>
                    <div className="row g-4">
                        <div className="col-md-4">
                            <div className="card h-100 border-0 shadow-sm">
                                <div className="card-body">
                                    <h5 className="card-title"><i className="fas fa-list-check me-2 text-primary"></i>Зручний
                                        облік</h5>
                                    <p className="card-text">Ведіть облік прочитаних книг та створюйте списки того, що
                                        хочете прочитати.</p>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card h-100 border-0 shadow-sm">
                                <div className="card-body">
                                    <h5 className="card-title"><i className="fas fa-lightbulb me-2 text-primary"></i>Персоналізовані
                                        рекомендації</h5>
                                    <p className="card-text">Отримуйте рекомендації на основі ваших читацьких уподобань
                                        та оцінок.</p>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card h-100 border-0 shadow-sm">
                                <div className="card-body">
                                    <h5 className="card-title"><i className="fas fa-comments me-2 text-primary"></i>Спільнота
                                    </h5>
                                    <p className="card-text">Діліться відгуками, читайте рецензії та взаємодійте з
                                        іншими читачами.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Популярні книги */}
            <section className="py-4">
                <div className="container my-4">
                    <h3 className="mb-4">Популярні книги</h3>
                    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                        {/* Книга 1: Бійцівський клуб */}
                        <div className="col">
                            <Link to="/books/298" className="text-decoration-none">
                                <div className="card book-card">
                                    <div className="book-img-container">
                                        <img
                                            src="https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1738004001i/30356725.jpg"
                                            className="book-img" alt="Бійцівський клуб - Чак Паланік"/>
                                    </div>
                                    <div className="card-body">
                                        <h5 className="card-title">Бійцівський клуб</h5>
                                        <p className="card-text">Чак Паланік</p>
                                        <div className="rating">
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star-half-alt"></i>
                                            <span className="text-dark ms-1">4.5</span>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>

                        {/* Книга 2: 1984 */}
                        <div className="col">
                            <Link to="/books/402" className="text-decoration-none">
                                <div className="card book-card">
                                    <div className="book-img-container">
                                        <img
                                            src="https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1691758554i/196264012.jpg"
                                            className="book-img" alt="1984 - Джордж Орвелл"/>
                                    </div>
                                    <div className="card-body">
                                        <h5 className="card-title">1984</h5>
                                        <p className="card-text">Джордж Орвелл</p>
                                        <div className="rating">
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="far fa-star"></i>
                                            <span className="text-dark ms-1">4.0</span>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>

                        {/* Книга 3: Я бачу, вас цікавить пітьма */}
                        <div className="col">
                            <Link to="/books/354" className="text-decoration-none">
                                <div className="card book-card">
                                    <div className="book-img-container">
                                        <img
                                            src="https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1722325874i/55168065.jpg"
                                            className="book-img" alt="Я бачу, вас цікавить пітьма - Іларіон Павлюк"/>
                                    </div>
                                    <div className="card-body">
                                        <h5 className="card-title">Я бачу, вас цікавить пітьма</h5>
                                        <p className="card-text">Іларіон Павлюк</p>
                                        <div className="rating">
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <i className="fas fa-star"></i>
                                            <span className="text-dark ms-1">5.0</span>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>

                        {/* Книга 4: Четверте крило */}
                        <div className="col">
                            <Link to="/books/8" className="text-decoration-none">
                            <div className="card book-card">
                                <div className="book-img-container">
                                    <img
                                        src="https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1702974437i/204033191.jpg"
                                        className="book-img" alt="Четверте крило - Ребекка Яррос"/>
                                </div>
                                <div className="card-body">
                                    <h5 className="card-title">Четверте крило</h5>
                                    <p className="card-text">Ребекка Яррос</p>
                                    <div className="rating">
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star"></i>
                                        <i className="fas fa-star-half-alt"></i>
                                        <i className="far fa-star"></i>
                                        <span className="text-dark ms-1">3.5</span>
                                    </div>
                                </div>
                            </div>
                            </Link>
                        </div>
                    </div>
                </div>
            </section>
            <Footer/>
        </>
    );
}

export default HomePage;