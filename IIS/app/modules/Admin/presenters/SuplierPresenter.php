<?php
namespace App\Admin\Presenters;

class SuplierPresenter extends BasePresenter
{
    function renderDefault(){
        bdump($this->user->isInRole('seller'));

        if(!$this->user->isAllowed('Suplier','view')){
            $this->flashMessage("Acess denied", "danger");
            $this->redirect('Homepage:default');
        }
    }
}