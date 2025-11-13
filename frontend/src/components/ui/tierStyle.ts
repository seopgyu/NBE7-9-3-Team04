export const tierStyles: Record<
  string,
  { color: string; bgColor: string; icon: string; gradient: string; shadow: string; animation: string }
> = {
  UNRATED: { 
    color: "text-gray-600", 
    bgColor: "bg-gray-100", 
    icon: "⚪",
    gradient: "bg-gradient-to-br from-gray-100 via-gray-50 to-gray-200",
    shadow: "shadow-md",
    animation: ""
  },
  BRONZE: { 
    color: "text-amber-700", 
    bgColor: "bg-amber-100", 
    icon: "🪙",
    gradient: "bg-gradient-to-br from-amber-300 via-orange-300 to-yellow-400",
    shadow: "shadow-lg shadow-amber-100/50",
    animation: "animate-pulse-slow"
  },
  SILVER: { 
    color: "text-gray-700", 
    bgColor: "bg-gray-200", 
    icon: "⚔️",
    gradient: "bg-gradient-to-br from-gray-300 via-slate-200 to-gray-400",
    shadow: "shadow-lg shadow-gray-100/60",
    animation: "animate-shimmer"
  },
  GOLD: { 
    color: "text-yellow-800", 
    bgColor: "bg-yellow-100", 
    icon: "🌟",
    gradient: "bg-gradient-to-br from-yellow-300 via-amber-300 to-orange-300",
    shadow: "shadow-xl shadow-yellow-100/70",
    animation: "animate-glow-gold"
  },
  PLATINUM: { 
    color: "text-cyan-700", 
    bgColor: "bg-cyan-100", 
    icon: "💠",
    gradient: "bg-gradient-to-br from-cyan-300 via-teal-300 to-emerald-300",
    shadow: "shadow-xl shadow-cyan-100/70",
    animation: "animate-glow-platinum"
  },
  DIAMOND: { 
    color: "text-blue-800", 
    bgColor: "bg-blue-100", 
    icon: "💎",
    gradient: "bg-gradient-to-br from-blue-400 via-indigo-300 to-purple-400",
    shadow: "shadow-2xl shadow-blue-100/80",
    animation: "animate-glow-diamond"
  },
  RUBY: { 
    color: "text-red-800", 
    bgColor: "bg-red-100", 
    icon: "🔴",
    gradient: "bg-gradient-to-br from-red-400 via-pink-400 to-rose-400",
    shadow: "shadow-2xl shadow-red-100/80",
    animation: "animate-glow-ruby"
  },
  MASTER: { 
    color: "text-purple-900", 
    bgColor: "bg-purple-100", 
    icon: "👑",
    gradient: "bg-gradient-to-br from-purple-500 via-pink-400 to-indigo-500",
    shadow: "shadow-2xl shadow-purple-100/90",
    animation: "animate-rainbow"
  },
};


// 티어별 테두리 스타일
export const tierBorderStyles: Record<string, string> = {
    UNRATED: "border-2 border-gray-300 bg-white",
    BRONZE:
      "border-transparent bg-gradient-to-r from-amber-500 via-orange-400 to-yellow-500 border-gray-200 animate-pulse-slow",
    SILVER:
      "border-transparent bg-gradient-to-r from-gray-400 via-slate-300 to-gray-500 border-gray-200 animate-shimmer",
    GOLD:
      "border-transparent bg-gradient-to-r from-yellow-400 via-amber-400 to-orange-500 border-gray-200 animate-glow-gold",
    PLATINUM:
      "border-transparent bg-gradient-to-r from-cyan-400 via-teal-400 to-emerald-500 border-gray-200 animate-glow-platinum",
    DIAMOND:
      "border-transparent bg-gradient-to-r from-blue-500 via-indigo-500 to-purple-600 border-gray-200 animate-glow-diamond",
    RUBY:
      "border-transparent bg-gradient-to-r from-red-500 via-pink-500 to-rose-600 border-gray-200 animate-glow-ruby",
    MASTER:
      "border-transparent bg-gradient-to-r from-purple-600 via-pink-600 to-indigo-700 border-gray-200 animate-rainbow",
  };
  
  // 프로필 아바타 테두리 스타일
  export const tierAvatarStyles: Record<string, string> = {
    UNRATED: "ring-2 ring-gray-300",
    BRONZE: "ring-4 ring-amber-200 shadow-lg shadow-amber-100/50",
    SILVER: "ring-4 ring-gray-200 shadow-lg shadow-gray-100/50",
    GOLD: "ring-4 ring-yellow-200 shadow-xl shadow-yellow-100/70",
    PLATINUM: "ring-4 ring-cyan-200 shadow-xl shadow-cyan-100/70",
    DIAMOND: "ring-4 ring-blue-200 shadow-2xl shadow-blue-100/80",
    RUBY: "ring-4 ring-red-200 shadow-2xl shadow-red-100/80",
    MASTER: "ring-[5px] ring-purple-200 shadow-2xl shadow-purple-100/90 animate-rainbow-ring",
  };