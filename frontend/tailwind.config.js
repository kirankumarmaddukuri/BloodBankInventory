/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#C0153E',
        primaryHover: '#E8314F',
        navy: '#1A1A2E',
        offWhite: '#F7F8FA',
        surface: '#FFFFFF',
        emergency: '#D44D00',
        urgent: '#B07D00',
        available: '#1A7A4A',
        reserved: '#185FA5',
        expired: '#6B6B6B',
        discarded: '#A32D2D',
      }
    },
  },
  plugins: [],
}
