<?php

namespace App\Front\Presenters;


use App\Model\ProductFacade;

class OrderPresenter extends BasePresenter
{

    private $facade;

    /**
     * OrderPresenter constructor.
     *
     * @param ProductFacade $facade
     */
    public function __construct(ProductFacade $facade)
    {
        $this->facade = $facade;
    }

    function renderDefault(){
        $items = $this->facade->getAllProduct();
        $this->template->items = $items;
    }
}