import {useState} from 'react';
import {deleteRating, saveRating} from '../api/ratings';

/**
 * props:
 *  - bookId
 *  - initial   (number|null)
 *  - onChange(score|null)  // повідомляє батьківський компонент
 *  - size='1.4rem'
 */
export default function StarRating({bookId, initial, onChange, size = '1.4rem'}) {
    const [hovered, setHovered] = useState(null);
    const [value, setValue] = useState(initial);   // null | 1..5

    const current = hovered ?? value ?? 0;

    async function click(i) {
        if (i === value) {                       // «та сама зірка» → прибрати
            await deleteRating(bookId);
            setValue(null);
            onChange?.(null);
        } else {
            await saveRating(bookId, i);
            setValue(i);
            onChange?.(i);
        }
    }

    return (
        <div
            className="rating"
            style={{fontSize: size, cursor: 'pointer'}}
            onMouseLeave={() => setHovered(null)}
        >
            {Array.from({length: 5}).map((_, i) => {
                const idx = i + 1;
                return (
                    <i
                        key={idx}
                        className={idx <= current ? 'fas fa-star' : 'far fa-star'}
                        onMouseEnter={() => setHovered(idx)}
                        onClick={() => click(idx)}
                    />
                );
            })}
        </div>
    );
}
