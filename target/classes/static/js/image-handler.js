// Image handling utilities - DISABLED to prevent loops
console.log('Image handler loaded but disabled to prevent redirect loops');

// Simple utility functions only
function getImageUrl(imageUrl) {
    if (!imageUrl) {
        return '/images/default-watch.svg';
    }
    
    if (imageUrl.startsWith('http')) {
        return imageUrl;
    }
    
    return '/images/' + imageUrl;
}

// Disabled all event listeners temporarily
/*
document.addEventListener('DOMContentLoaded', function() {
    // ALL CODE DISABLED TO PREVENT LOOPS
});
*/
