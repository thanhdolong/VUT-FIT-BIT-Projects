<?php

namespace App\Admin\Presenters;

use Nette;
use Nette\Security\IUserStorage;


/**
 * Base presenter for all application presenters.
 */
abstract class BasePresenter extends Nette\Application\UI\Presenter
{

    /**
     *
     */
    function startup()
    {
        parent::startup();
        $this->template->checkrole = $this->user->isAllowed('Dashboard','edit');

        if ($this->user->getLogoutReason() === IUserStorage::INACTIVITY) {
            if ($this->getUser()->isLoggedIn()) {
                $this->getUser()->logout();
                $this->flashMessage('Session timeout, you have been logged out', 'danger');
                $this->redirect('Sign:in');
            }
        }

        if (!$this->isLogged()) {
            $this->flashMessage("Access denied.", "danger");
            $this->redirect('Sign:in');
        } else {
            if (!empty($this->user->getIdentity()->username)) {
                $this->template->username = $this->user->getIdentity()->username;
            }
        }
    }

    protected function isLogged(): bool
    {
        return $this->getUser()->isLoggedIn();
    }

}
