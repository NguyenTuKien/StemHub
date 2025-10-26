document.addEventListener('DOMContentLoaded', function() {
    // Featured files horizontal scroll functionality
    const featuredContainer = document.querySelector('.featured-scroll-container');
    const featuredGrid = document.querySelector('.featured-grid');
    
    if (featuredContainer && featuredGrid) {
        let isScrolling = false;
        let startX = 0;
        let scrollLeft = 0;
        
        // Touch events for mobile
        featuredContainer.addEventListener('touchstart', function(e) {
            startX = e.touches[0].pageX - featuredContainer.offsetLeft;
            scrollLeft = featuredContainer.scrollLeft;
        });
        
        featuredContainer.addEventListener('touchmove', function(e) {
            if (!startX) return;
            e.preventDefault();
            const x = e.touches[0].pageX - featuredContainer.offsetLeft;
            const walk = (x - startX) * 2;
            featuredContainer.scrollLeft = scrollLeft - walk;
        });
        
        featuredContainer.addEventListener('touchend', function() {
            startX = 0;
        });
        
        // Mouse events for desktop
        featuredContainer.addEventListener('mousedown', function(e) {
            isScrolling = true;
            startX = e.pageX - featuredContainer.offsetLeft;
            scrollLeft = featuredContainer.scrollLeft;
            featuredContainer.style.cursor = 'grabbing';
            featuredContainer.style.userSelect = 'none';
        });
        
        featuredContainer.addEventListener('mousemove', function(e) {
            if (!isScrolling) return;
            e.preventDefault();
            const x = e.pageX - featuredContainer.offsetLeft;
            const walk = (x - startX) * 2;
            featuredContainer.scrollLeft = scrollLeft - walk;
        });
        
        featuredContainer.addEventListener('mouseup', function() {
            isScrolling = false;
            featuredContainer.style.cursor = 'grab';
            featuredContainer.style.userSelect = 'auto';
        });
        
        featuredContainer.addEventListener('mouseleave', function() {
            isScrolling = false;
            featuredContainer.style.cursor = 'grab';
            featuredContainer.style.userSelect = 'auto';
        });
        
        // Auto-scroll indicators
        const cards = featuredGrid.querySelectorAll('.featured-card');
        if (cards.length > 3) {
            // Add scroll indicators
            const indicators = document.createElement('div');
            indicators.className = 'scroll-indicators';
            
            // Calculate number of indicators based on cards
            const numIndicators = Math.min(5, Math.ceil(cards.length / 2));
            for (let i = 0; i < numIndicators; i++) {
                const indicator = document.createElement('div');
                indicator.className = 'scroll-indicator';
                if (i === 0) indicator.classList.add('active');
                indicators.appendChild(indicator);
            }
            
            featuredContainer.parentNode.appendChild(indicators);
            
            // Update indicators on scroll
            featuredContainer.addEventListener('scroll', function() {
                const scrollPercent = featuredContainer.scrollLeft / (featuredContainer.scrollWidth - featuredContainer.clientWidth);
                const activeIndex = Math.round(scrollPercent * (indicators.children.length - 1));
                
                Array.from(indicators.children).forEach((indicator, index) => {
                    indicator.classList.toggle('active', index === activeIndex);
                });
                
                // Update scroll state for gradient
                if (featuredContainer.scrollLeft > 0) {
                    featuredContainer.classList.add('scrolled');
                } else {
                    featuredContainer.classList.remove('scrolled');
                }
            });
        }
        
        // Smooth scroll to next/previous cards
        let currentIndex = 0;
        const cardWidth = cards[0]?.offsetWidth + 32; // card width + gap
        
        function scrollToCard(index) {
            if (index >= 0 && index < cards.length) {
                currentIndex = index;
                featuredContainer.scrollTo({
                    left: index * cardWidth,
                    behavior: 'smooth'
                });
            }
        }
        
        // Add navigation buttons if there are many cards
        if (cards.length > 4) {
            const navContainer = document.createElement('div');
            navContainer.className = 'scroll-navigation';
            navContainer.innerHTML = `
                <button class="scroll-nav-btn scroll-prev" aria-label="Previous">
                    <i class="fas fa-chevron-left"></i>
                </button>
                <button class="scroll-nav-btn scroll-next" aria-label="Next">
                    <i class="fas fa-chevron-right"></i>
                </button>
            `;
            featuredContainer.parentNode.appendChild(navContainer);
            
            // Navigation button events
            navContainer.querySelector('.scroll-prev').addEventListener('click', function() {
                scrollToCard(Math.max(0, currentIndex - 1));
            });
            
            navContainer.querySelector('.scroll-next').addEventListener('click', function() {
                scrollToCard(Math.min(cards.length - 1, currentIndex + 1));
            });
            
            // Update navigation buttons state
            featuredContainer.addEventListener('scroll', function() {
                const prevBtn = navContainer.querySelector('.scroll-prev');
                const nextBtn = navContainer.querySelector('.scroll-next');
                
                prevBtn.disabled = featuredContainer.scrollLeft <= 0;
                nextBtn.disabled = featuredContainer.scrollLeft >= featuredContainer.scrollWidth - featuredContainer.clientWidth - 10;
            });
        }
        
        // Keyboard navigation
        document.addEventListener('keydown', function(e) {
            if (e.key === 'ArrowLeft') {
                e.preventDefault();
                scrollToCard(Math.max(0, currentIndex - 1));
            } else if (e.key === 'ArrowRight') {
                e.preventDefault();
                scrollToCard(Math.min(cards.length - 1, currentIndex + 1));
            }
        });
    }
    
    // Enhanced card hover effects
    const featuredCards = document.querySelectorAll('.featured-card');
    featuredCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
    
    // Lazy loading for images
    const images = document.querySelectorAll('.featured-card-image img');
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    if (img.dataset.src) {
                        img.src = img.dataset.src;
                        img.classList.remove('lazy');
                        img.classList.add('loaded');
                    }
                    imageObserver.unobserve(img);
                }
            });
        });
        
        images.forEach(img => {
            if (img.dataset.src) {
                img.classList.add('lazy');
                imageObserver.observe(img);
            }
        });
    }
    
    // Fix card layout issues
    function fixCardLayout() {
        const cards = document.querySelectorAll('.featured-card');
        cards.forEach(card => {
            const content = card.querySelector('.featured-card-content');
            const footer = card.querySelector('.featured-card-footer');
            
            if (content && footer) {
                // Ensure footer stays at bottom
                content.style.display = 'flex';
                content.style.flexDirection = 'column';
                content.style.height = '100%';
                
                // Push footer to bottom
                const description = content.querySelector('.featured-card-description');
                if (description) {
                    description.style.flexGrow = '1';
                }
            }
        });
    }
    
    // Call fix function on load and resize
    fixCardLayout();
    window.addEventListener('resize', fixCardLayout);
    
    // Prevent text selection during drag
    featuredContainer?.addEventListener('selectstart', function(e) {
        if (isScrolling) {
            e.preventDefault();
        }
    });
}); 