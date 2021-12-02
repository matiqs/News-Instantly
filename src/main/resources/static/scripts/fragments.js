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

/*PUBLICATION*/

/*const lastNewDropdown = document.querySelectorAll(".last-new-dropdown-icon");
lastNewDropdown.forEach((element, index)=>{
	element.id = index;
})*/

const showLastNewDropdown = async (event)=>{
	const menu = await event.path[1].childNodes[3];
	menu.classList.toggle("hidden");
}

const hiddenLastNewDropdown = async (event)=>{
	const menu = await event.path[3];
	menu.classList.toggle("hidden");
}


/*ERROR Y SUCCES | INICIO Y PERFIL */
const errorContainer = document.querySelector(".error-container");
const succesContainer = document.querySelector(".succes-container");


const hiddenError = ()=> {
	errorContainer.classList.toggle("hidden");
}

const hiddenSucces = ()=> {
	succesContainer.classList.toggle("hidden");
}