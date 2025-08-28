import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { chapterService } from '../services/api';
import './ChapterReader.css';

const ChapterReader = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [chapter, setChapter] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [fontSize, setFontSize] = useState(16);
  const [fontFamily, setFontFamily] = useState('Arial');
  const [theme, setTheme] = useState('light');

  useEffect(() => {
    fetchChapter();
  }, [id]);

  const fetchChapter = async () => {
    try {
      setLoading(true);
      const response = await chapterService.getChapterWithContent(id);
      setChapter(response.data);
      
      // Lưu lịch sử đọc
      const userId = localStorage.getItem('userId');
      if (userId) {
        await chapterService.markAsRead(id, userId);
      }
      
      setLoading(false);
    } catch (error) {
      setError('Không thể tải nội dung chương');
      setLoading(false);
    }
  };

  const handleNextChapter = async () => {
    try {
      const response = await chapterService.getNextChapter(id);
      if (response.data) {
        navigate(`/chapter/${response.data.id}`);
      }
    } catch (error) {
      console.error('Không có chương tiếp theo');
    }
  };

  const handlePreviousChapter = async () => {
    try {
      const response = await chapterService.getPreviousChapter(id);
      if (response.data) {
        navigate(`/chapter/${response.data.id}`);
      }
    } catch (error) {
      console.error('Không có chương trước đó');
    }
  };

  const formatContent = (content) => {
    if (!content) return '';
    
    return content.split('\n').map((paragraph, index) => {
      if (paragraph.trim() === '') {
        return <br key={index} />;
      }
      return <p key={index}>{paragraph}</p>;
    });
  };

  if (loading) return <div className="loading">Đang tải...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!chapter) return <div>Không tìm thấy chương</div>;

  return (
    <div className={`chapter-reader ${theme}`}>
      <div className="reader-header">
        <button 
          className="back-button"
          onClick={() => navigate(`/story/${chapter.story.id}`)}
        >
          ← Quay lại truyện
        </button>
        
        <h1 className="chapter-title">{chapter.title}</h1>
        
        <div className="reader-controls">
          <div className="font-controls">
            <button onClick={() => setFontSize(f => Math.max(12, f - 2))}>A-</button>
            <span>Cỡ chữ: {fontSize}px</span>
            <button onClick={() => setFontSize(f => f + 2)}>A+</button>
          </div>
          
          <div className="font-family-controls">
            <select 
              value={fontFamily} 
              onChange={(e) => setFontFamily(e.target.value)}
            >
              <option value="Arial">Arial</option>
              <option value="Times New Roman">Times New Roman</option>
              <option value="Georgia">Georgia</option>
              <option value="Courier New">Courier New</option>
            </select>
          </div>
          
          <div className="theme-controls">
            <button 
              className={theme === 'light' ? 'active' : ''}
              onClick={() => setTheme('light')}
            >
              Sáng
            </button>
            <button 
              className={theme === 'dark' ? 'active' : ''}
              onClick={() => setTheme('dark')}
            >
              Tối
            </button>
            <button 
              className={theme === 'sepia' ? 'active' : ''}
              onClick={() => setTheme('sepia')}
            >
              Sepia
            </button>
          </div>
        </div>
      </div>
      
      <div className="chapter-navigation">
        <button 
          onClick={handlePreviousChapter}
          disabled={!chapter.previousChapterId}
          className="nav-button"
        >
          ← Chương trước
        </button>
        
        <select 
          value={chapter.id}
          onChange={(e) => navigate(`/chapter/${e.target.value}`)}
          className="chapter-select"
        >
          {chapter.chaptersList && chapter.chaptersList.map(ch => (
            <option key={ch.id} value={ch.id}>
              Chương {ch.chapterNumber}: {ch.title}
            </option>
          ))}
        </select>
        
        <button 
          onClick={handleNextChapter}
          disabled={!chapter.nextChapterId}
          className="nav-button"
        >
          Chương sau →
        </button>
      </div>
      
      <div 
        className="chapter-content"
        style={{ fontSize: `${fontSize}px`, fontFamily: fontFamily }}
      >
        {formatContent(chapter.content)}
      </div>
      
      <div className="chapter-footer">
        <div className="chapter-meta">
          <p>Truyện: <strong>{chapter.story.title}</strong></p>
          <p>Tác giả: <strong>{chapter.story.author}</strong></p>
          <p>Số từ: <strong>{chapter.wordCount?.toLocaleString()}</strong></p>
          <p>Lượt xem: <strong>{chapter.views?.toLocaleString()}</strong></p>
        </div>
        
        <div className="chapter-navigation bottom">
          <button 
            onClick={handlePreviousChapter}
            disabled={!chapter.previousChapterId}
            className="nav-button"
          >
            ← Chương trước
          </button>
          
          <button 
            onClick={handleNextChapter}
            disabled={!chapter.nextChapterId}
            className="nav-button"
          >
            Chương sau →
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChapterReader;