"use client";

import React, { useState, useRef, useEffect } from "react";

/* ✅ 공통 props 타입 */
interface DropdownMenuProps {
  children: React.ReactNode;
}

/* ✅ DropdownMenu wrapper */
export function DropdownMenu({ children }: DropdownMenuProps) {
  return <div className="relative inline-block text-left">{children}</div>;
}

/* ✅ Trigger */
export function DropdownMenuTrigger({
  children,
}: {
  children: React.ReactNode;
}) {
  return <>{children}</>;
}

/* ✅ Content (className 확장 및 스타일 수정) */
export function DropdownMenuContent({
  children,
  open,
  align = "end",
  className = "",
}: {
  children: React.ReactNode;
  open: boolean;
  align?: "start" | "end";
  className?: string; // ✅ 추가됨
}) {
  if (!open) return null;

  return (
    <div
      className={`absolute mt-1.5 min-w-[140px] rounded-md bg-white border border-gray-200 shadow-md z-50 
      ${align === "end" ? "right-0" : "left-0"} 
      ${className}`}
    >
      {children}
    </div>
  );
}

/* ✅ Item (여백 줄이고 className 허용) */
export function DropdownMenuItem({
  children,
  onClick,
  className = "",
}: {
  children: React.ReactNode;
  onClick?: () => void;
  className?: string;
}) {
  return (
    <button
      onClick={onClick}
      className={`block w-full text-left px-3 py-1.5 text-sm text-gray-700 hover:bg-gray-100 transition-colors ${className}`}
    >
      {children}
    </button>
  );
}

/* ✅ useDropdown Hook */
export function useDropdown() {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return { ref, open, setOpen };
}