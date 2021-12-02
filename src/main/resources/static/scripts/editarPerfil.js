const advancedConfig = document.querySelector(".Advanced-configuration");

const viewAdvancedConfig = () => {
  advancedConfig.classList.toggle("hidden");
};

const formOne = document.querySelector(".form-1");

const viewPassword = () => {
    formOne.classList.toggle("hidden");
};

const formTwo = document.querySelector(".form-2");

const viewDelete = () => {
    formTwo.classList.toggle("hidden");
};
