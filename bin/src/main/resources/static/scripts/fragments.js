/*HEADER*/
const showDropdown = () => {
  const dropdown = document.querySelector(".dropdown");
  dropdown.classList.toggle("hidden");
};

/*POST*/
const showCreateNews = () => {
  const createNew = document.querySelector(".create-news");
  createNew.classList.toggle("hidden");

  const createNewBtn = document.querySelector(".post-btn");
  createNewBtn.classList.toggle("hidden");
};