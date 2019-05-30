<?php

namespace App\Front\Forms;

use Nette;
use Nette\Application\UI\Form;


class commentFormFactory extends Nette\Application\UI\Presenter
{
    use Nette\SmartObject;

    /** @var FormFactory */
    private $factory;
    private $database;
    private $user;
    private $productId;

    public function __construct(FormFactory $factory, Nette\Database\Context $database, \Nette\Security\User $user)
    {
        $this->factory = $factory;
        $this->database = $database;
        $this->user = $user;

    }

    /**
     * @return Form
     */
    public function create($productId, callable $onSuccess)
    {
        $this->productId = $productId;
        $form = $this->factory->create();

        $form->addTextArea('content', 'Komentář:')
            ->setRequired();

        $form->addSubmit('send', 'Publikovat komentář');

        $form->onSuccess[] = function (Form $form, $values) use ($onSuccess) {
            try {
                bdump($this->getUser());
                bdump($this->productId);
                $this->database->table('recenzia')->insert([
                    'cisloproduktu_id' => $this->productId,
                    'username' => $this->getUser()->getIdentity()->username,
                    'content' => $values->content,
                ]);
            } catch (Nette\Security\AuthenticationException $e) {
                $form->addError('We cannot add your post.');
                return;
            }
            $onSuccess();
        };

        return $form;
    }
}
