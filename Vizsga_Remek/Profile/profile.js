// Alapértelmezett értékek
const defaultProfile = {
    name: "Kiss János",
    email: "janos.kiss@example.com",
    profilePic: "/img/profile_default.png",
  };
  
  // Local Storage-ból adat betöltése vagy alapértelmezett beállítása
  function loadProfile() {
    const savedProfile = JSON.parse(localStorage.getItem("profile")) || defaultProfile;
    document.getElementById("profileName").textContent = savedProfile.name;
    document.getElementById("profileEmail").textContent = savedProfile.email;
    document.getElementById("profilePic").src = savedProfile.profilePic;
  }
  
  // Adatok mentése Local Storage-ba
  function saveProfile() {
    const name = document.getElementById("nameInput").value;
    const email = document.getElementById("emailInput").value;
  
    // Profilkép frissítése
    const profilePicInput = document.getElementById("profilePicInput");
    const profilePic = profilePicInput.files.length
      ? URL.createObjectURL(profilePicInput.files[0])
      : document.getElementById("profilePic").src;
  
    const updatedProfile = { name, email, profilePic };
    localStorage.setItem("profile", JSON.stringify(updatedProfile));
  
    // Frissített értékek megjelenítése
    loadProfile();
  
    // Űrlap elrejtése
    document.getElementById("editForm").style.display = "none";
  }
  
  // Szerkesztés megnyitása
  document.getElementById("editButton").addEventListener("click", () => {
    document.getElementById("editForm").style.display = "block";
    const currentName = document.getElementById("profileName").textContent;
    const currentEmail = document.getElementById("profileEmail").textContent;
  
    document.getElementById("nameInput").value = currentName;
    document.getElementById("emailInput").value = currentEmail;
  });
  
  // Mentés gomb eseménykezelő
  document.getElementById("saveButton").addEventListener("click", saveProfile);
  
  // Oldal betöltésekor az adatok inicializálása
  document.addEventListener("DOMContentLoaded", loadProfile);


  // Alapértelmezett értékek visszaállítása
document.getElementById("resetButton").addEventListener("click", () => {
    localStorage.removeItem("profile");
    loadProfile();
  });
  