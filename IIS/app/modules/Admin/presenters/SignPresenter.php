<?php

namespace App\Admin\Presenters;

use App\Admin\Forms;
use Nette\Application\UI\Form;


class SignPresenter extends BasePresenter
{
	/** @var Forms\SignInFormFactory */
	private $signInFactory;

	/** @var Forms\SignUpFormFactory */
	private $signUpFactory;


    /**
     * SignPresenter constructor.
     * @param Forms\SignInFormFactory $signInFactory
     * @param Forms\SignUpFormFactory $signUpFactory
     */
    public function __construct(Forms\SignInFormFactory $signInFactory, Forms\SignUpFormFactory $signUpFactory)
	{
		$this->signInFactory = $signInFactory;
		$this->signUpFactory = $signUpFactory;
	}

    protected function isLogged(): bool
    {
        return true;
    }

	/**
	 * Sign-in form factory.
	 * @return Form
	 */
	protected function createComponentSignInForm()
	{
		return $this->signInFactory->create(function () {
			$this->redirect('Homepage:default');
		});
	}


	/**
	 * Sign-up form factory.
	 * @return Form
	 */
	protected function createComponentSignUpForm()
	{
		return $this->signUpFactory->create(function () {
            $this->flashMessage("You have been registered.", "success");
			$this->redirect('Sign:in');
		});
	}

    function actionIn(){
        if ($this->getUser()->isLoggedIn()) {
            $this->redirect('Homepage:default');
        }
    }

    function actionUp(){
        if ($this->getUser()->isLoggedIn()) {
            $this->redirect('Homepage:default');
        }
    }

	public function actionOut(){
        if ($this->getUser()->isLoggedIn()) {
            $this->getUser()->logout();
            $this->flashMessage("You have been signed out", "success");
            $this->redirect('Sign:in');
        }
	}
}
