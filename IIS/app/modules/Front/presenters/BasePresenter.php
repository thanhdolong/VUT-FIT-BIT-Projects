<?php

namespace App\Front\Presenters;

use Nette;


/**
 * Base presenter for all application presenters.
 */
abstract class BasePresenter extends Nette\Application\UI\Presenter
{
    function startup() {
        parent::startup();

        $session = $this->getSession('kosik');

        bdump($session->zbozi);
        $this->template->countItems = count($session['counter']);
    }

    public function handleDelete() {
        $this->session->destroy();
        $this->redirect("this");
    }
}
