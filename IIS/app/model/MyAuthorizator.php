<?php
/**
 * Created by PhpStorm.
 * User: thanh.dolong
 * Date: 29/09/2017
 * Time: 00:26
 */

namespace App\Model;
use Nette\Security\Permission;

/**
 * Users Authorizator.
 */
class AuthorizatorFactory
{
    private $acl;
    /**
     * @return Permission
     */
    public static function create()
    {
        $acl = new Permission();
        // definujeme role
        $acl->addRole('guest');
        $acl->addRole('registered', 'guest');

        $acl->addRole('seller');
        $acl->addRole('admin', 'seller');


        $acl->addResource('Products');
        $acl->addResource('Suplier');
        $acl->addResource('My Orders');
        $acl->addResource('Dashboard');

        $acl->allow('registered', 'My Orders');

        $acl->allow('seller', 'Suplier', 'view');
        $acl->allow('admin', Permission::ALL);
        $acl->deny('admin','My Orders');

        return $acl;
    }

    function isAllowed($role, $resource, $privilege):bool {
        return $this->acl->isAllowed($role, $resource, $privilege);
    }
}