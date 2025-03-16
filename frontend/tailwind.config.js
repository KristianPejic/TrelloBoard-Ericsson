/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        primary: {
          light: "#4dabf5",
          DEFAULT: "#2196f3",
          dark: "#1769aa",
        },
        secondary: {
          light: "#83c3f7",
          DEFAULT: "#64b5f6",
          dark: "#467eac",
        },
        background: "#f5f5f5",
        surface: "#ffffff",
        error: "#f44336",
        success: "#4caf50",
        warning: "#ff9800",
      },
    },
  },
  plugins: [],
};
