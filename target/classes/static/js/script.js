// Main JavaScript file for Đăng Quang Watch
console.log('Đăng Quang Watch - Main script loaded');

// Smooth image loading to prevent flashing
document.addEventListener('DOMContentLoaded', function() {
    // Preload critical images
    const images = document.querySelectorAll('img[data-src]');
    images.forEach(img => {
        const src = img.getAttribute('data-src');
        if (src) {
            img.src = src;
            img.removeAttribute('data-src');
        }
    });

    // Simple cart count update without page reload
    function updateCartCount() {
        // This could be implemented with AJAX in the future
        // For now, just ensure no unnecessary reloads
    }

    // Prevent form double submission
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                setTimeout(() => {
                    submitBtn.disabled = false;
                }, 3000);
            }
        });
    });

    // Smooth quantity updates
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(input => {
        input.addEventListener('change', function() {
            // Debounce updates to prevent flashing
            clearTimeout(this.updateTimeout);
            this.updateTimeout = setTimeout(() => {
                // Update logic here if needed
            }, 500);
        });
    });
});

// Utility function for smooth image fallback
function handleImageError(img) {
    console.log('Image error for:', img.src);
    if (!img.hasAttribute('data-fallback-applied')) {
        console.log('Applying fallback image');
        img.src = '/images/default-watch.svg';
        img.setAttribute('data-fallback-applied', 'true');
        
        // Add error styling class
        img.classList.add('watch-image-error');
        
        // Add title to indicate it's a placeholder
        img.title = 'Ảnh sản phẩm không có sẵn';
        
        // If SVG also fails, try a simple data URL
        img.onerror = function() {
            if (!this.hasAttribute('data-final-fallback')) {
                this.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRjhGOUZBIi8+CjxjaXJjbGUgY3g9IjEwMCIgY3k9IjEwMCIgcj0iNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iIzZDNzU3RCIgc3Ryb2tlLXdpZHRoPSIyIi8+CjxsaW5lIHgxPSIxMDAiIHkxPSIxMDAiIHgyPSIxMDAiIHkyPSI3MCIgc3Ryb2tlPSIjNkM3NTdEIiBzdHJva2Utd2lkdGg9IjIiLz4KPHN2ZyBzdHJva2U9IiM2Qzc1N0QiIGZpbGw9Im5vbmUiIHN0cm9rZS13aWR0aD0iMS41IiB2aWV3Qm94PSIwIDAgMjQgMjQiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgaGVpZ2h0PSIyNCIgd2lkdGg9IjI0IiB4PSI4OCIgeT0iMTI1Ij4KPHA+XHVC6SAiP3A+PHBhdGggZD0iTTE0LjUgNGgtNWwtMiA2LjVMMTUgMTguNSIvPjwvc3ZnPgo8dGV4dCB4PSIxMDAiIHk9IjE2MCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjEyIiBmaWxsPSIjNkM3NTdEIj5Eb25nIEhvPC90ZXh0Pgo8L3N2Zz4=';
                this.setAttribute('data-final-fallback', 'true');
            }
        };
    }
}

// Helper function to get correct image URL (support both HTTP and relative paths)
function getWatchImageUrl(imageUrl) {
    console.log('Processing image URL:', imageUrl);
    
    if (!imageUrl || imageUrl.trim() === '' || imageUrl === 'null' || imageUrl === 'undefined') {
        console.log('Empty or null image URL, using default');
        return '/images/default-watch.svg';
    }
    
    // Clean the URL
    imageUrl = imageUrl.trim();
    
    // If it's a full HTTP/HTTPS URL, use as-is
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
        console.log('HTTP URL detected:', imageUrl);
        return imageUrl;
    }
    
    // If it already starts with /images/, use as-is
    if (imageUrl.startsWith('/images/')) {
        console.log('Relative path with /images/ detected:', imageUrl);
        return imageUrl;
    }
    
    // If it starts with images/ (missing slash), add slash
    if (imageUrl.startsWith('images/')) {
        console.log('Relative path without leading slash detected:', imageUrl);
        return '/' + imageUrl;
    }
    
    // Otherwise, assume it's a filename and prepend /images/watches/
    console.log('Filename detected, adding /images/watches/ prefix:', imageUrl);
    return '/images/watches/' + imageUrl;
}

// Apply image URL correction to all watch images on page load
document.addEventListener('DOMContentLoaded', function() {
    const watchImages = document.querySelectorAll('img[data-watch-image]');
    watchImages.forEach(img => {
        const originalSrc = img.src;
        const correctedSrc = getWatchImageUrl(img.getAttribute('data-original-url') || originalSrc);
        if (correctedSrc !== originalSrc) {
            img.src = correctedSrc;
        }
    });
});

// Debug image URLs on page load
document.addEventListener('DOMContentLoaded', function() {
    const watchImages = document.querySelectorAll('img[data-watch-image]');
    console.log('Found', watchImages.length, 'watch images');
    
    watchImages.forEach((img, index) => {
        const originalUrl = img.getAttribute('data-original-url');
        const currentSrc = img.src;
        
        console.log(`Image ${index + 1}:`, {
            originalUrl: originalUrl,
            currentSrc: currentSrc,
            isEmpty: !originalUrl || originalUrl.trim() === '',
            alt: img.alt
        });
        
        // If image URL is empty or problematic, preemptively apply default
        if (!originalUrl || originalUrl.trim() === '' || originalUrl === 'null' || originalUrl === 'undefined') {
            console.log('Preemptively applying default image for image', index + 1);
            img.src = '/images/default-watch.svg';
            img.classList.add('watch-image-error');
            img.title = 'Ảnh sản phẩm không có sẵn';
        }
    });
});

// Optimize Bootstrap components initialization
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips if any
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});
