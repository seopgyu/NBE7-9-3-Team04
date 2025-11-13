import React from "react";

// ✅ Table 전체
export function Table({ children }: { children: React.ReactNode }) {
  return (
    <table className="w-full border-collapse border border-gray-200 rounded-lg text-sm">
      {children}
    </table>
  );
}

// ✅ Header
export function TableHeader({ children }: { children: React.ReactNode }) {
  return <thead className="bg-gray-50 border-b border-gray-200">{children}</thead>;
}

// ✅ Head 셀
export function TableHead({ children }: { children: React.ReactNode }) {
  return (
    <th className="px-4 py-2 text-left text-sm font-semibold text-gray-600">
      {children}
    </th>
  );
}

// ✅ Body
export function TableBody({ children }: { children: React.ReactNode }) {
  return <tbody className="divide-y divide-gray-100">{children}</tbody>;
}

// ✅ Row (🔥 수정됨 — className, onClick 등 허용)
export function TableRow({
  children,
  className = "",
  ...props
}: React.HTMLAttributes<HTMLTableRowElement>) {
  return (
    <tr
      className={`hover:bg-gray-50 transition ${className}`}
      {...props} // ✅ onClick, style 등 추가 props 허용
    >
      {children}
    </tr>
  );
}

// ✅ Cell
export function TableCell({
  children,
  className = "",
  ...props
}: React.TdHTMLAttributes<HTMLTableCellElement>) {
  return (
    <td
      className={`px-4 py-3 text-gray-700 text-sm align-middle ${className}`}
      {...props}
    >
      {children}
    </td>
  );
}
