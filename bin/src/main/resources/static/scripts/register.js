const Registrarme = () => {
  const response = grecaptcha.getResponse();
  if (response.length != 0) {
  } else
    document.getElementById("status").innerHTML = "Acepta el captcha primero !";
};
