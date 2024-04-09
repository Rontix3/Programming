import logo from './logo.svg';
import './App.css';

function App() {
  
  const handleSearch = (e) => {
    e.preventDefault();
    // Itt lehetne implementálni a keresési logikát
    console.log("Keresés indítása:", e.target.search.value);
  };

  return (
    <div className="App">
      <header className="App-header">
        
        {/* Navigációs sáv a jobb oldalon */}
        <nav className="Nav-container">
          
          {/* Logo a navigációs sávban */}
          <img src={logo} className="App-logo" alt="logo" />
          
          {/* Navigációs lista */}
          <ul className="Nav-list">
            <li className="Nav-item">
              <a href="/" className="Nav-link">Home</a>
            </li>
            <li className="Nav-item">
              <a href="/about" className="Nav-link">About</a>
            </li>
            <li className="Nav-item">
              <a href="/blog" className="Nav-link">Blog</a>
            </li>
            <li className="Nav-item">
              <a href="/contact" className="Nav-link">Contact</a>
            </li>
          </ul>

          {/* Keresőbar a navigációs sávban */}
          <form onSubmit={handleSearch} className="Search-form">
            <input 
              type="text" 
              name="search" 
              placeholder="Keresés..."
              className="Search-input"
            />
            <button type="submit" className="Search-button">Keresés</button>
          </form>
        
        </nav>
        
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;
