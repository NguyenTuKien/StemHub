// Add CSRF token for AJAX requests
document.addEventListener('DOMContentLoaded', function() {
    // Create CSRF token element if it doesn't exist
    if (!document.querySelector('[name=csrfmiddlewaretoken]')) {
        const csrfToken = document.createElement('input');
        csrfToken.type = 'hidden';
        csrfToken.name = 'csrfmiddlewaretoken';
        csrfToken.value = '{{ csrf_token }}';
        document.body.appendChild(csrfToken);
    }
    
    // Enhanced notification handling
    enhanceNotifications();

    // Like button wiring (toggle like/unlike)
    const likeBtn = document.getElementById('likeBtn');
    if (likeBtn) {
        likeBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            const documentId = likeBtn.getAttribute('data-document-id');
            const userId = likeBtn.getAttribute('data-user-id');
            let liked = String(likeBtn.getAttribute('data-liked')) === 'true';

            if (!userId) {
                // Not logged in -> redirect to login
                window.location.href = '/auth/login';
                return;
            }
            if (!documentId) return;

            // UI: disable and show spinner (preserve label and icon)
            const iconEl = likeBtn.querySelector('i');
            const labelEl = document.getElementById('likeLabel');
            const originalIconClass = iconEl ? iconEl.className : '';
            const originalLabel = labelEl ? labelEl.textContent : '';
            likeBtn.disabled = true;
            if (labelEl) labelEl.textContent = 'Đang xử lý...';
            if (iconEl) iconEl.className = 'fas fa-spinner fa-spin me-2';

            try {
                const params = new URLSearchParams({ userId, documentId });
                const res = await fetch('/like', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: params.toString(),
                    redirect: 'follow'
                });

                if (!res.ok) throw new Error('Like request failed');

                // Toggle UI state
                const likeCountEl = document.getElementById('likeCount');
                const current = parseInt(likeCountEl?.textContent || '0', 10) || 0;
                if (liked) {
                    // Was liked -> now unlike
                    const newCount = Math.max(0, current - 1);
                    if (likeCountEl) likeCountEl.textContent = String(newCount);
                    likeBtn.classList.remove('btn-danger');
                    likeBtn.classList.add('btn-outline-danger');
                    if (iconEl) iconEl.className = 'far fa-heart me-2';
                    if (labelEl) labelEl.textContent = 'Yêu thích';
                    likeBtn.setAttribute('data-liked', 'false');
                    liked = false;
                } else {
                    // Was not liked -> now like
                    if (likeCountEl) likeCountEl.textContent = String(current + 1);
                    likeBtn.classList.remove('btn-outline-danger');
                    likeBtn.classList.add('btn-danger');
                    if (iconEl) iconEl.className = 'fas fa-heart me-2';
                    if (labelEl) labelEl.textContent = 'Bỏ yêu thích';
                    likeBtn.setAttribute('data-liked', 'true');
                    liked = true;
                }
            } catch (err) {
                console.error(err);
                // Restore UI on error
                if (iconEl) iconEl.className = originalIconClass;
                if (labelEl) labelEl.textContent = originalLabel || 'Yêu thích';
                alert('Không thể cập nhật yêu thích. Vui lòng thử lại.');
            } finally {
                likeBtn.disabled = false;
            }
        });
    }

    // Download button wiring
    const downloadBtn = document.getElementById('downloadBtn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', async (e) => {
            // We want to count download then proceed to actual file
            e.preventDefault();
            const documentId = downloadBtn.getAttribute('data-document-id');
            const fileUrl = downloadBtn.getAttribute('data-file-url') || downloadBtn.getAttribute('href');
            if (!documentId || !fileUrl) {
                // fallback: navigate to file
                window.open(downloadBtn.getAttribute('href'), '_blank');
                return;
            }

            const originalHtml = downloadBtn.innerHTML;
            downloadBtn.classList.add('disabled');
            downloadBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang tải...';

            try {
                const params = new URLSearchParams({ documentId });
                await fetch('/download', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: params.toString(),
                    redirect: 'follow'
                });
                // Even if not ok, proceed with download to not block user
                const downloadCountEl = document.getElementById('downloadCount');
                if (downloadCountEl) {
                    const current = parseInt(downloadCountEl.textContent || '0', 10) || 0;
                    downloadCountEl.textContent = String(current + 1);
                }
            } catch (err) {
                console.warn('Download count failed, proceeding anyway:', err);
            } finally {
                // Proceed to actual file download in new tab
                try {
                    const a = document.createElement('a');
                    a.href = fileUrl;
                    a.target = '_blank';
                    a.rel = 'noopener';
                    a.download = '';
                    document.body.appendChild(a);
                    a.click();
                    a.remove();
                } catch (openErr) {
                    // fallback
                    window.open(fileUrl, '_blank');
                }
                downloadBtn.classList.remove('disabled');
                downloadBtn.innerHTML = originalHtml;
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    // Hide loading overlay after a delay if onload events don't trigger
    setTimeout(hideLoading, 3000);
    
    // Debug video loading
    const video = document.querySelector('video');
    if (video) {
        console.log('Video element found:', video.src);
        
        video.addEventListener('loadstart', () => {
            console.log('Video: Load started');
        });
        
        video.addEventListener('loadedmetadata', () => {
            console.log('Video: Metadata loaded');
        });
        
        video.addEventListener('loadeddata', () => {
            console.log('Video: Data loaded');
            hideLoading();
        });
        
        video.addEventListener('canplay', () => {
            console.log('Video: Can play');
            hideLoading();
        });
        
        video.addEventListener('error', (e) => {
            console.error('Video error:', e);
            console.error('Video error details:', video.error);
            hideLoading();
        });
    }
});

function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

function showVideoFallback(videoElement) {
    console.error('Video failed to load, showing fallback');
    hideLoading();
    
    // Create fallback content
    const fallbackHTML = `
        <div class="video-fallback">
            <div class="video-placeholder">
                <i class="fas fa-exclamation-triangle fa-3x mb-3" style="color: #dc3545;"></i>
                <h5>Không thể phát video</h5>
                <p class="text-muted mb-3">Video có thể bị lỗi hoặc định dạng không được hỗ trợ</p>
                <a href="${videoElement.querySelector('source').src}" 
                   class="btn btn-primary" 
                   target="_blank">
                    <i class="fas fa-download me-2"></i>Tải xuống video
                </a>
            </div>
        </div>
    `;
    
    // Replace video with fallback
    const videoViewer = videoElement.closest('.video-viewer');
    if (videoViewer) {
        videoViewer.innerHTML = fallbackHTML;
    }
}

function showAdvancedFallback(videoElement) {
    console.error('Advanced video failed to load, showing fallback options');
    hideLoading();
    
    // Hide the video element
    if (videoElement) {
        videoElement.style.display = 'none';
    }
    
    // Show the final fallback
    const container = videoElement.closest('.advanced-video-container');
    if (container) {
        const finalFallback = container.querySelector('.final-fallback');
        if (finalFallback) {
            finalFallback.style.display = 'block';
        }
    }
}

function tryAlternativeViewers(buttonElement) {
    const container = buttonElement.closest('.advanced-video-container');
    if (container) {
        const alternativeViewers = container.querySelector('.alternative-viewers');
        const finalFallback = container.querySelector('.final-fallback');
        
        if (alternativeViewers && finalFallback) {
            // Hide final fallback and show alternative viewers
            finalFallback.style.display = 'none';
            alternativeViewers.style.display = 'block';
            
            // Add back button
            const backButton = document.createElement('button');
            backButton.className = 'btn btn-outline-secondary mb-3';
            backButton.innerHTML = '<i class="fas fa-arrow-left me-2"></i>Quay lại';
            backButton.onclick = function() {
                alternativeViewers.style.display = 'none';
                finalFallback.style.display = 'block';
            };
            alternativeViewers.insertBefore(backButton, alternativeViewers.firstChild);
        }
    }
}

function openInNewTab(url) {
    window.open(url, '_blank');
}

// Add video format detection
function detectVideoFormat(filename) {
    const extension = filename.toLowerCase().split('.').pop();
    const formats = {
        'mp4': { type: 'video/mp4', supported: true },
        'webm': { type: 'video/webm', supported: true },
        'avi': { type: 'video/x-msvideo', supported: false },
        'mov': { type: 'video/quicktime', supported: false },
        'wmv': { type: 'video/x-ms-wmv', supported: false },
        'flv': { type: 'video/x-flv', supported: false },
        'mkv': { type: 'video/x-matroska', supported: false }
    };
    
    return formats[extension] || { type: 'video/mp4', supported: false };
}

function shareFile() {
    if (navigator.share) {
        navigator.share({
            title: '{{ file.title }}',
            text: '{{ file.file_description|truncatechars:100 }}',
            url: window.location.href
        });
    } else {
        // Fallback - copy to clipboard
        navigator.clipboard.writeText(window.location.href).then(() => {
            alert('Đã sao chép liên kết vào clipboard!');
        });
    }
}

function reportFile() {
    if (confirm('Bạn có chắc chắn muốn báo cáo tài liệu này?')) {
        alert('Cảm ơn bạn đã báo cáo. Chúng tôi sẽ xem xét trong thời gian sớm nhất.');
    }
}

function getCookie(name) {
    let cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            // cookie = "csrftoken=abc123"
            if (cookie.substring(0, name.length + 1) === (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

function toggleFavorite() {
    const fileId = document.getElementById('favoriteBtn')?.getAttribute('data-file-id');
    const btn = document.getElementById('favoriteBtn');
    const icon = document.getElementById('favoriteIcon');
    const text = document.getElementById('favoriteText');
    
    if (!fileId || !btn || !icon || !text) return;

    fetch(`/toggle-favorite/${fileId}/`, {
        method: 'POST',
        headers: {
            'X-CSRFToken': getCookie('csrftoken'),
            'Content-Type': 'application/json',
        },
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            if (data.is_favorited) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                text.textContent = 'Bỏ yêu thích';
                btn.classList.remove('btn-outline-danger');
                btn.classList.add('btn-danger');
            } else {
        icon.classList.remove('fas');
        icon.classList.add('far');
                text.textContent = 'Thêm vào yêu thích';
                btn.classList.remove('btn-danger');
                btn.classList.add('btn-outline-danger');
            }
            
            // Show success message
            const toast = document.createElement('div');
            toast.className = 'alert alert-success position-fixed';
            toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999;';
            toast.textContent = data.message;
            document.body.appendChild(toast);
            
            setTimeout(() => {
                toast.remove();
            }, 3000);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra. Vui lòng thử lại.');
    });
}

// Track file views (could be enhanced with analytics)
if (typeof gtag !== 'undefined') {
    gtag('event', 'file_view', {
        'file_title': '{{ file.title }}',
        'file_author': '{{ file.author.username }}'
    });
}

// Enhanced notification system
function enhanceNotifications() {
    const alerts = document.querySelectorAll('.alert');
    
    alerts.forEach((alert, index) => {
        // Add staggered animation delay
        alert.style.animationDelay = `${index * 0.15}s`;
        
        // Auto-hide after 6 seconds for info messages (trừ điểm)
        if (alert.classList.contains('alert-info')) {
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.style.animation = 'slideOutRight 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards';
                    setTimeout(() => {
                        if (alert.parentNode) {
                            alert.remove();
                        }
                    }, 500);
                }
            }, 6000);
        }
        
        // Auto-hide after 8 seconds for success messages (cộng điểm)
        if (alert.classList.contains('alert-success')) {
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.style.animation = 'slideOutRight 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards';
                    setTimeout(() => {
                        if (alert.parentNode) {
                            alert.remove();
                        }
                    }, 500);
                }
            }, 8000);
        }
        
        // Enhanced close button with smooth animation
        const closeBtn = alert.querySelector('.btn-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', function() {
                alert.style.animation = 'slideOutRight 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards';
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.remove();
                    }
                }, 500);
            });
        }
        
        // Add subtle entrance effect
        alert.style.opacity = '0';
        alert.style.transform = 'translateX(100%) scale(0.95)';
        
        setTimeout(() => {
            alert.style.transition = 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)';
            alert.style.opacity = '1';
            alert.style.transform = 'translateX(0) scale(1)';
        }, index * 150);
    });
}

// Add slideOutRight animation to CSS
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOutRight {
        from {
            transform: translateX(0) scale(1);
            opacity: 1;
        }
        to {
            transform: translateX(100%) scale(0.95);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);