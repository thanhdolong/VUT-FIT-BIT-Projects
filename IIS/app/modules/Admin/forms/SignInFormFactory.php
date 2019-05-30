<?php

namespace App\Admin\Forms;

use Nette;
use Nette\Application\UI\Form;
use Nette\Security\User;


class SignInFormFactory
{
	use Nette\SmartObject;

	/** @var FormFactory */
	private $factory;

	/** @var User */
	private $user;


	public function __construct(FormFactory $factory, User $user)
	{
		$this->factory = $factory;
		$this->user = $user;
	}

	/**
	 * @return Form
	 */
	public function create(callable $onSuccess)
	{

		$form = $this->factory->create();
		$form->addText('username')
			->setRequired('Please enter your username.')
            ->setHtmlAttribute('class', 'input-block-level')
            ->setHtmlAttribute('placeholder', 'Username');

		$form->addPassword('password')
			->setRequired('Please enter your password.')
            ->setHtmlAttribute('placeholder', 'Password')
            ->setHtmlAttribute('class', 'input-block-level');


		$form->addCheckbox('remember', 'Keep me signed in');

		$form->addSubmit('send', 'Sign in');


		$form->onSuccess[] = function (Form $form, $values) use ($onSuccess) {
			try {
				$this->user->setExpiration($values->remember ? '14 days' : '20 minutes');
				$this->user->login($values->username, $values->password);
			} catch (Nette\Security\AuthenticationException $e) {
                $form->addError('The username or password you entered is incorrect.');
                return;
			}
			$onSuccess();
		};

		return $form;
	}
}
