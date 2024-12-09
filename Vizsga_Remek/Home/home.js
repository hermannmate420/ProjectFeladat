// Figyelünk a karusszel elemek váltására
document.querySelectorAll('.carousel').forEach(function(carousel) {
    carousel.addEventListener('slid.bs.carousel', function() {
      let activeItem = carousel.querySelector('.carousel-item.active');
      let description = activeItem.querySelector('.carousel-description');
      
      // Lehetőség van a leírás módosítására vagy más változtatásokra
      // Például színt változtatni
      description.style.color = 'blue';
    });
  });