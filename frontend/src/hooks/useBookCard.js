import {useNavigate} from "react-router-dom";
import {useCallback, useMemo} from "react";

export function useBookCard(book) {
    const navigate = useNavigate();

    const rating = useMemo(() => Math.round(book.averageRating * 2) / 2, [book.averageRating]);

    const renderStar = useCallback(
        (i) => {
            if (i + 1 <= rating) return "fas fa-star";
            if (i + 0.5 === rating) return "fas fa-star-half-alt";
            return "far fa-star";
        },
        [rating]
    );

    const handleCardClick = useCallback(
        (e) => {
            if (e.target.closest("a, button")) return;
            navigate(`/books/${book.id}`);
        },
        [book.id, navigate]
    );

    const handleKeyDown = useCallback(
        (e) => {
            if (e.key === "Enter" || e.key === " ") {
                navigate(`/books/${book.id}`);
            }
        },
        [book.id, navigate]
    );

    return {rating, renderStar, handleCardClick, handleKeyDown};
}
