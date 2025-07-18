import React, { useState, useEffect } from 'react';
import { postReview, deleteReview } from '../../api/reviews';
import './ReviewModal.css';
import {deleteRating, postRating} from "../../api/rating";

function ReviewModal({ book, onClose, onSave }) {
    const [review, setReview] = useState('');
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0); // Track hover state for stars
    const [isOpen, setIsOpen] = useState(false);
    const [isSaving, setIsSaving] = useState(false);

    // Initialize modal data when a book is received
    useEffect(() => {
        if (book) {
            setIsOpen(true);
            setReview(book.review || '');
            setRating(book.myRating || 0);
        } else {
            setIsOpen(false);
        }
    }, [book]);

    const handleClose = () => {
        setIsOpen(false);
        // Allow animation to complete before removing from DOM
        setTimeout(() => {
            onClose();
        }, 300);
    };

    // Handle rating change and API call
    const handleRatingChange = async (newRating) => {
        if (!book) return;

        const oldRating = rating;
        const finalRating = oldRating === newRating ? 0 : newRating; // Toggle off if clicking same star

        // Update UI immediately for better UX
        setRating(finalRating);

        try {
            if (finalRating === 0) {
                await deleteRating(book.id);
            } else {
                await postRating(book.id, finalRating);
            }
        } catch (error) {
            console.error('Error updating rating:', error);
            // Revert UI if API call fails
            setRating(oldRating);
        }
    };

    const handleSave = async () => {
        if (!book) return;

        setIsSaving(true);

        try {
            // Save the review if there's content
            if (review.trim()) {
                await postReview(book.id, review);
            } else if (book.review) {
                // Delete the review if it existed but now is empty
                await deleteReview(book.id);
            }

            onSave({
                ...book,
                review: review.trim() || null,
                myRating: rating
            });

            handleClose();
        } catch (error) {
            console.error('Error saving review:', error);
        } finally {
            setIsSaving(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="review-modal-backdrop">
            <div className="review-modal">
                <div className="review-modal-header">
                    <h5 className="modal-title">
                        {book?.review ? 'Редагувати рецензію' : 'Додати рецензію'}
                    </h5>
                    <button
                        type="button"
                        className="btn-close"
                        onClick={handleClose}
                        aria-label="Close"
                    ></button>
                </div>

                <div className="review-modal-body">
                    <div className="book-info mb-3">
                        <img src={book?.cover} alt={book?.title} className="book-cover-small me-3" />
                        <div>
                            <h6>{book?.title}</h6>
                            <p className="text-muted">{book?.author}</p>
                        </div>
                    </div>

                    <div className="rating-section mb-3">
                        <label className="form-label">Ваша оцінка:</label>
                        <div className="rating-stars-large">
                            {Array.from({ length: 5 }).map((_, i) => (
                                <i
                                    key={i}
                                    className={
                                        i < rating
                                            ? "fas fa-star"
                                            : i < hoverRating
                                                ? "fas fa-star star-hover"
                                                : "far fa-star"
                                    }
                                    style={{ cursor: "pointer" }}
                                    onClick={() => handleRatingChange(i + 1)}
                                    onMouseEnter={() => setHoverRating(i + 1)}
                                    onMouseLeave={() => setHoverRating(0)}
                                />
                            ))}
                        </div>
                    </div>

                    <div className="review-section">
                        <label htmlFor="reviewContent" className="form-label">Ваша рецензія:</label>
                        <textarea
                            id="reviewContent"
                            className="form-control"
                            rows="6"
                            value={review}
                            onChange={(e) => setReview(e.target.value)}
                            placeholder="Поділіться своїми враженнями про книгу..."
                        ></textarea>
                    </div>
                </div>

                <div className="review-modal-footer">
                    <button
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={handleClose}
                    >
                        Скасувати
                    </button>
                    <button
                        type="button"
                        className="btn btn-primary"
                        onClick={handleSave}
                        disabled={isSaving}
                    >
                        {isSaving ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                Збереження...
                            </>
                        ) : (
                            'Зберегти'
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ReviewModal;