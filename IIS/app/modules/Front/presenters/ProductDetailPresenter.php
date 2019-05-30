<?php
namespace App\Front\Presenters;

use App\Model\ProductFacade;
use App\Front\Forms;

class ProductDetailPresenter extends BasePresenter
{
    private $facade;
    private $commentFactory;
    private $productId;


    /**
     * HomepagePresenter constructor.
     *
     * @param ProductFacade            $facade
     * @param Forms\commentFormFactory $commentFactory
     */
    public function __construct(ProductFacade $facade, Forms\commentFormFactory $commentFactory)
    {
        $this->facade = $facade;
        $this->commentFactory = $commentFactory;
    }

    protected function createComponentCommentForm()
    {
        $this->productId = $this->getPresenter()->getParameter('productId');

        return $this->commentFactory->create($this->productId, function () {
            $this->redirect('this');
        }
        );
    }

    public function handleAddToCart($productId) {
        $session = $this->getSession('kosik');

        if(!isset($session->zbozi[$productId])) $session->zbozi[$productId] = null;
        $session->zbozi[$productId]++;


        if(is_array($session->counter)) {
            $session->counter = array_merge($session->counter, [$productId]);
        }
        else {
            $session->counter = [$productId];
        }


        $this->redirect("this");
    }

    function renderDefault(int $productId){

        $product = $this->facade->getProductById($productId);
        if (! $product) {
            $this->error();
        }

        $this->template->comments = $product->related('recenzia')->order('created_at DESC');
        $this->template->detailpastelek = $product->ref('baleniepastelek','cisloproduktu_id');
        $this->template->detailskicar = $product->ref('skicar','cisloproduktu_id');

        $this->template->product = $product;
    }
}