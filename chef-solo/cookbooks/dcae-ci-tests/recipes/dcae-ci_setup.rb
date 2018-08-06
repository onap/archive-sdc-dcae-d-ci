url = "#{node['Webseal_URL']}"
be_vip = node['DCAE_BE_VIP']
fe_vip = node['DCAE_FE_VIP']

if node['disableHttp']
    protocol = "https"
    be_port = node['DCAE']['BE'][:https_port]
        fe_port = node['DCAE']['FE'][:https_port]
else
    protocol = "http"
    be_port = node['DCAE']['BE'][:http_port]
        fe_port = node['DCAE']['FE'][:http_port]
end

template "dcae-ci-tests-conf" do
 sensitive true
 path "/conf/conf.yaml"
 source "conf.yaml.erb"
 owner "dcae"
 group "dcae"
 mode "0755"
 variables ({
        :catalogbe_ip     => be_vip,
        :catalogbe_port   => be_port,
        :protocol         => protocol,
        :url              => url,
        :catalogfe_ip     => fe_vip,
        :catalogfe_port   => fe_port
 })
end