using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Security
{
    public class LotFertilization
    {
        public int Id { get; set; }
        public int LotId { get; set; }
        public Lot Lot { get; set; }
        public int FertilizationId { get; set; }
        public Fertilization Fertilization { get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
