"use client";

interface CategoryTabsProps {
  categories: string[];
  selected: string;
  onSelect: (category: string) => void;
}

export default function CategoryTab({
  categories,
  selected,
  onSelect,
}: CategoryTabsProps) {
  return (
    <>
      <div className="flex space-x-6 border-b border-gray-200 mb-6">
        {categories.map((category) => (
          <button
            key={category}
            onClick={() => onSelect(category)}
            className={`pb-2 text-base font-semibold transition-colors ${
              selected === category
                ? "text-blue-600 border-b-2 border-blue-600"
                : "text-gray-600 hover:text-gray-900"
            }`}
          >
            {category}
          </button>
        ))}
      </div>
    </>
  );
}
