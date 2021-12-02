package com.NewsInstantly.RedSocialNoticias.controlador;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorsController implements ErrorController{

	@RequestMapping(value = "/error", method = {RequestMethod.GET,RequestMethod.POST})
	public String showErrorPage(ModelMap model, HttpServletRequest httpServletRequest) {
		String errorMessage="";
		int errorCod=(int)httpServletRequest.getAttribute("javax.servlet.error.status_code");
		
		switch(errorCod) {
		case 400:
			errorMessage="El recurso solicitado no existe.";
			break;
		case 401:
			errorMessage="No se encuentra autorizado.";
			break;
		case 403:
			errorMessage="No tiene permisos para acceder al recurso.";
			break;
		case 404:
			errorMessage="El recurso solicitado no se ha encontrado.";
			break;
		case 500:
			errorMessage="El servidor no pudo realizar la petición con éxito.";
			break;
		}
		model.put("code", errorCod);
		model.put("message", errorMessage);
		return "errorPage";
	}
}
