import React from 'react';

export default function Footer() {
    return (
        <footer className="mt-4">
            <div className="container py-4">
                <div className="row">
                    <div className="col-md-6">
                        <p><i className="fas fa-book-open me-2"></i><strong>Readict</strong></p>
                        <p>&copy; 2025 Readict. Всі права захищені.</p>
                    </div>
                    <div className="col-md-6 text-md-end">
                        <p>
                            <a href="#" className="text-decoration-none me-3 text-muted">Про нас</a>
                            <a href="#" className="text-decoration-none me-3 text-muted">Контакти</a>
                            <a href="#" className="text-decoration-none me-3 text-muted">Умови користування</a>
                        </p>
                        <p>
                            <a href="#" className="text-decoration-none me-2 text-muted"><i
                                className="fab fa-facebook"/></a>
                            <a href="#" className="text-decoration-none me-2 text-muted"><i className="fab fa-twitter"/></a>
                            <a href="#" className="text-decoration-none me-2 text-muted"><i
                                className="fab fa-instagram"/></a>
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    );
}
