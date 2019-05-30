<?php

namespace App\Model;

use Nette\Database\Context;
use Nette\Database\Table\Selection;

class ProductFacade
{
    /** @var Context */
    private $db;

    public function __construct(Context $db)
    {
        $this->db = $db;
    }

    public function getAllProduct(): Selection
    {
        return $this->db->table('produkt');
        //return $this->db->query('SELECT * FROM posts');
    }

    public function getProductById($id) {
        $result = $this->db->table('produkt')->get($id);

        return $result;
    }
}