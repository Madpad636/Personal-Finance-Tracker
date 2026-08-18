/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // A calm, trustworthy "ledger" palette instead of default blue/purple.
        ink: '#1B2430',       // primary text / dark surfaces
        paper: '#F7F5F0',     // app background
        ledger: {
          50: '#EEF3F1',
          100: '#D7E4DE',
          400: '#4C8577',
          500: '#2F6657',     // primary brand (deep teal-green, "money growing")
          600: '#26534A',
          700: '#1D3F38'
        },
        signal: {
          income: '#2F6657',   // positive / income
          expense: '#B24C3A',  // negative / expense (muted brick, not alarm red)
          warn: '#C98A2C'
        }
      },
      fontFamily: {
        display: ['"Fraunces"', 'serif'],
        body: ['"Inter"', 'sans-serif']
      }
    }
  },
  plugins: []
}
