using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Operational
{
    public class Fumigation
    {
        public int Id { get; set; }
        public DateTime DateFumigation {  get; set; }
        public string QuantityMix { get; set; }
        public int ReviewTechnicalId {  get; set; }
        public ReviewTechnical ReviewTechnical { get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
