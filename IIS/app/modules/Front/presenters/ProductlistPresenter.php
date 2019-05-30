<?php

namespace App\Front\Presenters;
use App\Model\ProductFacade;

class ProductlistPresenter extends BasePresenter
{

    private $facade;

    /**
     * HomepagePresenter constructor.
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
